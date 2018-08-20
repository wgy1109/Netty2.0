package com.huawei.smproxy;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.sgip.SGIPConnection;
import com.huawei.insa2.comm.sgip.SGIPTransaction;
import com.huawei.insa2.comm.sgip.SSEventListener;
import com.huawei.insa2.comm.sgip.SSListener;
import com.huawei.insa2.comm.sgip.message.SGIPDeliverMessage;
import com.huawei.insa2.comm.sgip.message.SGIPDeliverRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;
import com.huawei.insa2.comm.sgip.message.SGIPReportMessage;
import com.huawei.insa2.comm.sgip.message.SGIPReportRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUserReportMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUserReportRepMessage;
import com.huawei.insa2.util.Args;

public class SGIPSMProxy implements SSEventListener {
	private static final Logger log = LoggerFactory.getLogger(SGIPSMProxy.class);
	public interface SGIPMessageListener {

		public void onDeliver(SGIPDeliverMessage msg);

		public void onReport(SGIPReportMessage msg);
	}
	
	private SGIPConnection conn;

	private SSListener listener;

	private Args args;

	private HashMap<String,SGIPConnection> serconns;

	private int src_nodeid;
	
	private SGIPMessageListener messageListener=null;

	public SGIPSMProxy(Map args) {
		this(new Args(args));
	}

	public SGIPSMProxy(Args args) {
		this.args = args;
		src_nodeid = args.get("source-addr", 0);
	}

	public SGIPMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(SGIPMessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public synchronized boolean connect(String loginName, String loginPass) {
		if (loginName != null)
			args.set("login-name", loginName.trim());
		if (loginPass != null)
			args.set("login-pass", loginPass.trim());
		try{
			conn = new SGIPConnection(args, true, null);
			conn.addEventListener(new SGIPEventAdapter(this, conn));
			conn.waitAvailable();
		}
		catch(Exception e){
			log.error("loginName:"+loginName+" connect exception,",e);
		}
		return conn.available();
	}

	public synchronized void startService(String localhost, int localport) {
		if (listener != null)
			return;
		try {
			listener = new SSListener(localhost, localport, this);
			listener.beginListen();
		} catch (Exception exception) {
		}
	}

	public synchronized void stopService() {
		if (listener == null)
			return;
		listener.stopListen();
		if (serconns != null) {
			Set<String> connsKeys = serconns.keySet();
			for(String ks :connsKeys){
				SGIPConnection co = serconns.get(ks);
				co.stop();
			}
			serconns.clear();
		}
	}

	public synchronized void onConnect(Socket socket) {
		String peerIP = socket.getInetAddress().getHostAddress();
		int port = socket.getPort();
		if (serconns == null)
			serconns = new HashMap<String,SGIPConnection>();
		SGIPConnection conn = new SGIPConnection(args, false, serconns);
		conn.addEventListener(new SGIPEventAdapter(this, conn));
		conn.attach(args, socket);
		if(serconns.size()>0){
			Set<String> connsKeys = serconns.keySet();
			for(String ks :connsKeys){
				SGIPConnection co = serconns.get(ks);
				if(!co.available()){
					co.stop();
				}
			}
		}
		SGIPConnection cn = serconns.get(peerIP + port);
		if(cn!=null){
			cn.stop();
		}
		conn.setConName("Connection Name = "+peerIP +" : "+ port);
		serconns.put(peerIP + port, conn);
	}

	public SGIPMessage send(SGIPMessage message) throws IOException {
		if (message == null)
			return null;
		
		SGIPTransaction t = (SGIPTransaction) conn.createChild();
		t.setSPNumber(src_nodeid);
		Date nowtime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
		String tmpTime = dateFormat.format(nowtime);
		Integer timestamp = new Integer(tmpTime);
		t.setTimestamp(timestamp.intValue());
		try {
			conn.onWaitWindow();
			t.send(message);
			t.waitResponse();
			SGIPMessage rsp = t.getResponse();
			if(rsp!=null){
				conn.setWindowValue(0);
			}
			SGIPMessage sgipmessage = rsp;
			return sgipmessage;
		} finally {
			t.close();
		}
	}

	public String getServerStatus(){
		StringBuilder builder = new StringBuilder();
		if(serconns!=null && serconns.size()>0){
			Set<String> connsKeys = serconns.keySet();
			for(String ks :connsKeys){
				SGIPConnection co = serconns.get(ks);
				builder.append("["+co.getConName()+" - "+co.getError()+"]");
			}
		}
		return "[]";
	}
	
	public void onTerminate() {
	}

	public SGIPMessage onDeliver(SGIPDeliverMessage msg) {
		if (messageListener!=null) {
			messageListener.onDeliver(msg);
		}
		return new SGIPDeliverRepMessage(0);
	}

	public SGIPMessage onReport(SGIPReportMessage msg) {
		if (messageListener!=null) {
			messageListener.onReport(msg);
		}
		return new SGIPReportRepMessage(0);
	}

	public SGIPMessage onUserReport(SGIPUserReportMessage msg) {
		return new SGIPUserReportRepMessage(0);
	}

	public void stop(){
		conn.stop();
		stopService();
	}
	public void close() {
		conn.close();
	}

	public SGIPConnection getConn() {
		return conn;
	}
	
	public boolean available(){
		return conn!=null && conn.available() && conn.isCanSend();
	}
	
	public boolean isMockDisconnect(){
		return conn.isHasConnected() && (System.currentTimeMillis()-conn.getDisConnectTime())<10000;
	}
	
	public String getConnState() {
		return conn.getError()==null?"连接正常":conn.getError();
	}
	
	public void onSendReadTimeOut(){
		conn.onSendReadTimeOut();
	}
	
	public int getTps(){
		if(conn!=null){
			return conn.getTps();
		}
		return 0;
	}
	public void setTps(int tps){
		if(conn!=null){
			conn.setTps(tps);
		}
	}
}

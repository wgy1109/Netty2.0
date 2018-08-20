package com.huawei.smproxy;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.cmpp.CMPPConnection;
import com.huawei.insa2.comm.cmpp.CMPPTransaction;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverRepMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.util.Args;

public class SMProxy {
	// add by zyq at 20160427
	private static final Logger log = LoggerFactory.getLogger(SMProxy.class);
	
	public interface CMPPMessageListener {
		public void onDeliver(CMPPDeliverMessage msg);
		public void onReport(CMPPDeliverMessage msg);
	}
	private CMPPConnection conn;
	private CMPPMessageListener messageListener=null;

	public SMProxy(Map args) {
		this(new Args(args));
	}

	public SMProxy(Args args) {
		try{
			conn = new CMPPConnection(args);
			conn.addEventListener(new CMPPEventAdapter(this));
			conn.waitAvailable();
		}catch(Exception e){
			log.error("SMProxy constructor exception,",e);
		}
		
	}

	public CMPPMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(CMPPMessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public CMPPMessage send(CMPPMessage message) throws IOException {
		if (message == null)
			return null;
		CMPPTransaction t = (CMPPTransaction) conn.createChild();
		try {
			t.send(message);
			t.waitResponse();
			CMPPMessage rsp = t.getResponse();
			CMPPMessage cmppmessage = rsp;
			return cmppmessage;
		} finally {
			t.close();
		}
	}

	public void onTerminate() {
	}

	public CMPPMessage onDeliver(CMPPDeliverMessage msg) {
		if (this.messageListener!=null) {
			if (msg.getRegisteredDeliver()==1) {
				messageListener.onReport(msg);
			} else {
				messageListener.onDeliver(msg);
			}
		}
		return new CMPPDeliverRepMessage(msg.getMsgId(), 0);
	}
	public void stop() {
		conn.stop();
	}
	
	public CMPPConnection getConn(){
		return conn;
	}
	
	public boolean available(){
		return conn!=null && conn.available() && conn.isCanSend();
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

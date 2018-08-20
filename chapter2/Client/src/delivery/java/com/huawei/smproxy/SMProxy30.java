package com.huawei.smproxy;

import java.io.IOException;
import java.util.Map;

import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp30.CMPP30Connection;
import com.huawei.insa2.comm.cmpp30.CMPP30Transaction;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverRepMessage;
import com.huawei.insa2.util.Args;

public class SMProxy30 {
	public interface CMPP30MessageListener {
		public void onDeliver(CMPP30DeliverMessage msg);
		public void onReport(CMPP30DeliverMessage msg);
	}
	private CMPP30Connection conn;
	private CMPP30MessageListener messageListener=null;

	public SMProxy30(Map args) {
		this(new Args(args));
	}

	public SMProxy30(Args args) {
		conn = new CMPP30Connection(args);
		conn.addEventListener(new CMPP30EventAdapter(this));
		conn.waitAvailable();
		if (!conn.available())
			throw new IllegalStateException(conn.getError());
		else
			return;
	}

	public CMPP30MessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(CMPP30MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public CMPPMessage send(CMPPMessage message) throws IOException {
		if (message == null)
			return null;
		CMPP30Transaction t = (CMPP30Transaction) conn.createChild();
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

	public CMPPMessage onDeliver(CMPP30DeliverMessage msg) {
		if (this.messageListener!=null) {
			if (msg.getRegisteredDeliver()==1) {
				messageListener.onReport(msg);
			} else {
				messageListener.onDeliver(msg);
			}
		}
		return new CMPP30DeliverRepMessage(msg.getMsgId(), 0);
	}
	public void stop(){
		conn.stop();
	}
	public void close() {
		conn.close();
	}
	
	public CMPP30Connection getConn(){
		return this.conn;
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

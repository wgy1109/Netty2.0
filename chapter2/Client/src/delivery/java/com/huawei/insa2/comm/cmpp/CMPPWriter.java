package com.huawei.insa2.comm.cmpp;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PWriter;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitMessage;

public class CMPPWriter extends PWriter {
	private static final Logger log = LoggerFactory.getLogger(CMPPWriter.class);
	
	private Object lock = new Object();
	
	protected OutputStream out;

	public CMPPWriter(OutputStream out) {
		this.out = out;
		this.preSendFinishTime = System.currentTimeMillis();
	}

	public void write(PMessage message) throws IOException {
		CMPPMessage msg = (CMPPMessage) message;
		boolean isSubmitMessage = (message instanceof CMPPSubmitMessage) || (message instanceof CMPP30SubmitMessage);
		if(isSubmitMessage){
			synchronized(lock){
				long current = System.currentTimeMillis();
				long inter = current-preSendFinishTime;
				if(inter<intervalTime){
					try {
						Thread.sleep(intervalTime-inter);
					} catch (InterruptedException e) {
					}
				}
				write(msg.getBytes());
				this.preSendFinishTime = System.currentTimeMillis();
				addSendList(preSendFinishTime);
			}
		}
		else{
			write(msg.getBytes());
		}	
	}
	public synchronized void write(byte[] bytes) throws IOException {
		out.write(bytes);	
	}

	public void writeHeartbeat() throws IOException {
	}

}
package com.huawei.insa2.comm.cmpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.util.Debug;

public class CMPPTransaction extends PLayer {
	//add by zyq at 20160503
	private static final Logger log = LoggerFactory.getLogger(CMPPTransaction.class);
	
	private CMPPMessage receive;

	private int sequenceId;

	public CMPPTransaction(PLayer connection) {
		super(connection);
		sequenceId = super.id;
	}

	public synchronized void onReceive(PMessage msg) {
		receive = (CMPPMessage) msg;
		sequenceId = receive.getSequenceId();
		if (CMPPConstant.debug) {
			Debug.dump(receive.toString());
		}
		
		notifyAll();
	}

	public void send(PMessage message) throws PException {
		CMPPMessage mes = (CMPPMessage) message;
		mes.setSequenceId(sequenceId);
		super.parent.send(message);
		if (CMPPConstant.debug)
			Debug.dump(mes.toString());
	}

	public CMPPMessage getResponse() {
		return receive;
	}

	public synchronized void waitResponse() {
		if (receive == null)
			try {
				wait(((CMPPConnection) super.parent).getTransactionTimeout());
			} catch (InterruptedException interruptedexception) {
			}
	}

}

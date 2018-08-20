package com.huawei.smproxy;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.comm.smgp.SMGPConnection;
import com.huawei.insa2.comm.smgp.SMGPTransaction;
import com.huawei.insa2.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.insa2.comm.smgp.message.SMGPDeliverRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;
import com.huawei.insa2.util.Args;

public class SMGPSMProxy
{
	private static final Logger log = LoggerFactory.getLogger(SMGPSMProxy.class);

	public interface SMGPMessageListener {
		public void onDeliver(SMGPDeliverMessage msg);
		public void onReport(SMGPDeliverMessage msg);
	}

	private SMGPMessageListener messageListener=null;
    public SMGPSMProxy(Map args)
    {
        this(new Args(args));
    }

    public SMGPSMProxy(Args args)
    {
    	try{
	        conn = new SMGPConnection(args);
	        conn.addEventListener(new SMGPEventAdapter(this));
	        conn.waitAvailable();
    	}
    	catch(Exception e){
    		log.error("SMGPSMProxy constructor exception,",e);
    	}
    }
    
    public SMGPMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(SMGPMessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public SMGPMessage send(SMGPMessage message)
        throws IOException
    {
        if(message == null)
            return null;
        SMGPTransaction t = (SMGPTransaction)conn.createChild();
        try
        {
            t.send(message);
            t.waitResponse();
            SMGPMessage rsp = t.getResponse();
            SMGPMessage smgpmessage = rsp;
            return smgpmessage;
        }
        finally
        {
            t.close();
        }
    }

    public void onTerminate()
    {
    }

    public SMGPMessage onDeliver(SMGPDeliverMessage msg)
    {
    	if (this.messageListener!=null) {
    		if (msg.getIsReport()==1) {
    			this.messageListener.onReport(msg);
    		} else {
    			this.messageListener.onDeliver(msg);
    		}
    	}
        return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
    }

    public void stop(){
    	conn.stop();
    }
    public void close()
    {
        conn.close();
    }

    public SMGPConnection getConn()
    {
        return conn;
    }
    
	public boolean available(){
		return conn!=null && conn.available() && conn.isCanSend();
	}
	
    public String getConnState()
    {
    	return conn.getError()==null?"连接正常":conn.getError();
    }
    
	public void onSendReadTimeOut(){
		conn.onSendReadTimeOut();
	}
    private SMGPConnection conn;
    
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

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SMGPConnection.java

package com.huawei.insa2.comm.smgp;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.smgp.message.*;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

// Referenced classes of package com.huawei.insa2.comm.smgp:
//            SMGPWriter, SMGPReader, SMGPTransaction, SMGPConstant

public class SMGPConnection extends PSocketConnection
{
	private static final Logger log = LoggerFactory.getLogger(SMGPConnection.class);
	
    public SMGPConnection(Args args)
    {
        degree = 0;
        hbnoResponseOut = 3;
        clientid = null;
        hbnoResponseOut = args.get("heartbeat-noresponseout", 3);
        clientid = args.get("clientid", "huawei");
        version = 30;
        shared_secret = args.get("shared-secret", "");
        SMGPConstant.debug = args.get("debug", false);
        SMGPConstant.initConstant();
        init(args);
    }

    protected PWriter getWriter(OutputStream out)
    {
        return new SMGPWriter(out);
    }

    protected PReader getReader(InputStream in)
    {
        return new SMGPReader(in);
    }

    public int getChildId(PMessage message)
    {
        SMGPMessage mes = (SMGPMessage)message;
        int sequenceId = mes.getSequenceId();
        if(mes.getRequestId() == 3 || mes.getRequestId() == 4 || mes.getRequestId() == 6)
            return -1;
        else
            return sequenceId;
    }

    public PLayer createChild()
    {
        return new SMGPTransaction(this);
    }

    public int getTransactionTimeout()
    {
        return super.transactionTimeout;
    }

    public Resource getResource()
    {
        try
        {
            Resource resource = new Resource(getClass(), "resource");
            return resource;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        Resource resource1 = null;
        return resource1;
    }

    public synchronized void waitAvailable()
    {
        try
        {
            if(getError() == PSocketConnection.NOT_INIT)
                wait(super.transactionTimeout);
        }
        catch(InterruptedException interruptedexception) { }
    }

    public void stop(){
    	super.isManStop = true;
    	if(available() && canSend){
	        try
	        {
	        	SMGPExitMessage msg = new SMGPExitMessage();
	            send(msg);
	        }
	        catch(PException pexception) { 
	        	log.error("SMGPConnection[close]:{}",pexception);
	        }
    	}
		if (heartbeatThread != null){
			heartbeatThread.kill();
		}
		if(receiveThread != null){
		    receiveThread.kill();
		}
		super.close();
    }

    protected void heartbeat()
        throws IOException
    {
        SMGPTransaction t = (SMGPTransaction)createChild();
        SMGPActiveTestMessage hbmes = new SMGPActiveTestMessage();
        t.send(hbmes);
        t.waitResponse();
        SMGPActiveTestRespMessage rsp = (SMGPActiveTestRespMessage)t.getResponse();
        if(rsp == null)
        {
            degree++;
            if(degree == hbnoResponseOut)
            {
                degree = 0;
                throw new IOException(SMGPConstant.HEARTBEAT_ABNORMITY);
            }
        } else
        {
            degree = 0;
        }
        t.close();
    }

    protected synchronized void connect()
    {
    	canSend = false;
        super.connect();
        if(!available())
            return;
        SMGPLoginMessage request = null;
        SMGPLoginRespMessage rsp = null;
        try
        {
            request = new SMGPLoginMessage(clientid, shared_secret, 2, new Date(), version);
        }
        catch(IllegalArgumentException e)
        {
            e.printStackTrace();
            close();
            setError(SMGPConstant.CONNECT_INPUT_ERROR);
            notifyAll();
            return;
        }
        SMGPTransaction t = (SMGPTransaction)createChild();
        try
        {
            t.send(request);
            PMessage m = super.in.read();
            onReceive(m);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            close();
            setError(String.valueOf(SMGPConstant.LOGIN_ERROR) + String.valueOf(explain(e)));
            notifyAll();
            return;
        }
        rsp = (SMGPLoginRespMessage)t.getResponse();
        if(rsp == null)
        {
            close();
            setError(SMGPConstant.CONNECT_TIMEOUT);
        }
        t.close();
        if(rsp != null && rsp.getStatus() != 0)
        {
            close();
            setError("Fail to login,the status code is ".concat(String.valueOf(String.valueOf(rsp.getStatus()))));
        }
        if(rsp!=null  && rsp.getStatus() == 0){
    		canSend = true;
        }
        notifyAll();
    }

    private int degree;
    private int hbnoResponseOut;
    private String clientid;
    private int version;
    private String shared_secret;
}

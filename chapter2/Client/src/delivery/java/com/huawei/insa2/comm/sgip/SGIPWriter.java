// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SGIPWriter.java

package com.huawei.insa2.comm.sgip;

import java.io.IOException;
import java.io.OutputStream;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PWriter;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;
import com.huawei.insa2.comm.sgip.message.SGIPSubmitMessage;

public class SGIPWriter extends PWriter
{

    public SGIPWriter(OutputStream out)
    {
        this.out = out;
    }

    public synchronized void write(PMessage message)
        throws IOException
    {
        SGIPMessage msg = (SGIPMessage)message;
        
        
        boolean submitMessage = (message instanceof SGIPSubmitMessage);
		if(submitMessage){
			long current = System.currentTimeMillis();
			long inter = current-preSendFinishTime;
			if(inter<intervalTime){
				try {
					Thread.sleep(intervalTime-inter);
				} catch (InterruptedException e) {
				}
			}
		}
		out.write(msg.getBytes());
		
		if(submitMessage){
			this.preSendFinishTime = System.currentTimeMillis();
			addSendList(preSendFinishTime);
		}
    }

    public void writeHeartbeat()
        throws IOException
    {
    }

    protected OutputStream out;
}

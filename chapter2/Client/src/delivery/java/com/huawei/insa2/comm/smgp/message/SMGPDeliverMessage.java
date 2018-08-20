// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SMGPDeliverMessage.java

package com.huawei.insa2.comm.smgp.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.insa2.util.TypeConvert;

// Referenced classes of package com.huawei.insa2.comm.smgp.message:
//            SMGPMessage

public class SMGPDeliverMessage extends SMGPMessage
{

	private static final Logger log = LoggerFactory.getLogger(SMGPDeliverMessage.class);
	
    public SMGPDeliverMessage(byte buf[])
        throws IllegalArgumentException
    {
        
   
            super.buf = new byte[buf.length];
            System.arraycopy(buf, 0, super.buf, 0, buf.length);
            super.sequence_Id = TypeConvert.byte2int(super.buf, 0);
            return;
    }

    public byte[] getMsgId()
    {
        byte msgId[] = new byte[10];
        System.arraycopy(super.buf, 4, msgId, 0, 10);
        return msgId;
    }

    public int getIsReport()
    {
        return super.buf[14];
    }

    public int getMsgFormat()
    {
        return super.buf[15];
    }

    public String getSrcTermID()
    {
        byte srcTermId[] = new byte[21];
        System.arraycopy(super.buf, 30, srcTermId, 0, 21);
        return (new String(srcTermId)).trim();
    }

    public String getDestTermID()
    {
        byte destTermId[] = new byte[21];
        System.arraycopy(super.buf, 51, destTermId, 0, 21);
        return (new String(destTermId)).trim();
    }

    public int getMsgLength()
    {
        return super.buf[72] & 0xff;
    }
    
    public int getTpUdhi(){
    	int len = 81 + (buf[72] & 0xff);
    	if(super.buf.length>len){
    		return 1;
    	}
    	return 0;
    }

    public byte[] getMsgContent()
    {
        int len = super.buf[72] & 0xff;
        byte content[] = new byte[len];
        System.arraycopy(super.buf, 73, content, 0, len);
        return content;
    }

    public String getReserve()
    {
        int loc = 73 + (super.buf[72] & 0xff);
        byte reserve[] = new byte[8];
        System.arraycopy(super.buf, loc, reserve, 0, 8);
        return (new String(reserve)).trim();
    }

    public String toString()
    {
        StringBuffer strBuf = new StringBuffer(600);
        strBuf.append("SMGPDeliverMessage: ");
        strBuf.append("Sequence_Id=".concat(String.valueOf(String.valueOf(getSequenceId()))));
        //strBuf.append(",MsgID=".concat(String.valueOf(String.valueOf(new String(getMsgId())))));
        String msgId = ""; 
        if(getIsReport() == 1){
        	byte[] tmpmsgid = new byte[10];
			System.arraycopy(this.getMsgContent(), 3, tmpmsgid, 0, 10);
			msgId = String.valueOf(TypeConvert.getLong(tmpmsgid, 0));
        }
        else{
        	  byte tmpmsgid[] = new byte[10];
              System.arraycopy(super.buf, 4, tmpmsgid, 0, 10);
        	  msgId = String.valueOf(TypeConvert.getLong(tmpmsgid, 0));
        }
        
        strBuf.append(",MsgID=".concat(msgId));
        strBuf.append(",IsReport=".concat(String.valueOf(String.valueOf(getIsReport()))));
        strBuf.append(",MsgFormat=".concat(String.valueOf(String.valueOf(getMsgFormat()))));
        strBuf.append(",SrcTermID=".concat(String.valueOf(String.valueOf(getSrcTermID()))));
        strBuf.append(",DestTermID=".concat(String.valueOf(String.valueOf(getDestTermID()))));
        strBuf.append(",MsgLength=".concat(String.valueOf(String.valueOf(getMsgLength()))));
        strBuf.append(",MsgContent=".concat(String.valueOf(String.valueOf(new String(getMsgContent())))));
        strBuf.append(",reserve=".concat(String.valueOf(String.valueOf(getReserve()))));
        return strBuf.toString();
    }

    public int getRequestId()
    {
        return 3;
    }
}

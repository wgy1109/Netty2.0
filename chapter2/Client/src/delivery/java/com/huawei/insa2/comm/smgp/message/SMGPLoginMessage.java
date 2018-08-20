// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SMGPLoginMessage.java

package com.huawei.insa2.comm.smgp.message;

import java.util.Date;

import com.huawei.insa2.comm.smgp.SMGPConstant;
import com.huawei.insa2.util.DateUtil;
import com.huawei.insa2.util.SecurityTools;
import com.huawei.insa2.util.TypeConvert;

// Referenced classes of package com.huawei.insa2.comm.smgp.message:
//            SMGPMessage

public class SMGPLoginMessage extends SMGPMessage
{
	
    public SMGPLoginMessage(String clientId, String shared_Secret, int loginMode, Date timestamp, int version)
        throws IllegalArgumentException
    {
        if(clientId == null)
            throw new IllegalArgumentException(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(SMGPConstant.CONNECT_INPUT_ERROR)))).append(":clientId ").append(SMGPConstant.STRING_NULL))));
        if(clientId.length() > 8)
            throw new IllegalArgumentException(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(SMGPConstant.CONNECT_INPUT_ERROR)))).append(":clientId ").append(SMGPConstant.STRING_LENGTH_GREAT).append("8"))));
        if(loginMode < 0 || loginMode > 255)
            throw new IllegalArgumentException(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(SMGPConstant.CONNECT_INPUT_ERROR)))).append(":loginMode ").append(SMGPConstant.INT_SCOPE_ERROR))));
        if(version < 0 || version > 255)
            throw new IllegalArgumentException(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(SMGPConstant.CONNECT_INPUT_ERROR)))).append(":version ").append(SMGPConstant.INT_SCOPE_ERROR))));
        int len = 42;
        super.buf = new byte[len];
        TypeConvert.int2byte(len, super.buf, 0);
        TypeConvert.int2byte(1, super.buf, 4);
        System.arraycopy(clientId.getBytes(), 0, super.buf, 12, clientId.length());
        if(shared_Secret != null)
            len = clientId.length() + 17 + shared_Secret.length();
        else
            len = clientId.length() + 17;
        byte tmpbuf[] = new byte[len];
        int tmploc = 0;
        System.arraycopy(clientId.getBytes(), 0, tmpbuf, 0, clientId.length());
        tmploc = clientId.length() + 7;
        if(shared_Secret != null)
        {
            System.arraycopy(shared_Secret.getBytes(), 0, tmpbuf, tmploc, shared_Secret.length());
            tmploc += shared_Secret.length();
        }
        String tmptime = DateUtil.GetTimeString();
        System.arraycopy(tmptime.getBytes(), 0, tmpbuf, tmploc, 10);
        SecurityTools.md5(tmpbuf, 0, len, super.buf, 20);
        super.buf[36] = (byte)loginMode;
        TypeConvert.int2byte(Integer.parseInt(tmptime), super.buf, 37);
        super.buf[41] = (byte)version;
        strBuf = new StringBuffer(300);
        strBuf.append(",clientId=".concat(String.valueOf(String.valueOf(clientId))));
        strBuf.append(",shared_Secret=".concat(String.valueOf(String.valueOf(shared_Secret))));
        strBuf.append(",loginMode=".concat(String.valueOf(String.valueOf(loginMode))));
        strBuf.append(",version=".concat(String.valueOf(String.valueOf(version))));
    }

    public String toString()
    {
        StringBuffer outStr = new StringBuffer(300);
        outStr.append("SMGPLoginMessage:");
        outStr.append("PacketLength=".concat(String.valueOf(String.valueOf(super.buf.length))));
        outStr.append(",RequestID=1");
        outStr.append(",Sequence_Id=".concat(String.valueOf(String.valueOf(getSequenceId()))));
        if(strBuf != null)
            outStr.append(strBuf.toString());
        return outStr.toString();
    }

    public int getRequestId()
    {
        return 1;
    }

    private StringBuffer strBuf;
}

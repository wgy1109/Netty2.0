package com.huawei.insa2.comm.cmpp.message;

import com.huawei.insa2.comm.cmpp.CMPPConstant;
import com.huawei.insa2.util.TypeConvert;

public class CMPPTerminateMessage extends CMPPMessage {

	public CMPPTerminateMessage() {
		int len = 12;
		super.buf = new byte[len];
		TypeConvert.int2byte(len, super.buf, 0);
		TypeConvert.int2byte(CMPPConstant.Terminate_Command_Id, super.buf, 4);
	}

	public CMPPTerminateMessage(byte buf[]) throws IllegalArgumentException {
		if (buf.length != 4) {
			throw new IllegalArgumentException(CMPPConstant.SMC_MESSAGE_ERROR);
		}
		
		int len = 12;
		super.buf = new byte[len];
		TypeConvert.int2byte(len, super.buf, 0);
		
		System.arraycopy(buf, 0, super.buf, 8, 4);
		super.sequence_Id = TypeConvert.byte2int(buf, 0);
	}

	public String toString() {
		String tmpStr = "CMPP_Terminate: ";
		tmpStr = tmpStr + "Sequence_Id=" + getSequenceId();
		return tmpStr;
	}

	public int getCommandId() {
		return CMPPConstant.Terminate_Command_Id;
	}
}

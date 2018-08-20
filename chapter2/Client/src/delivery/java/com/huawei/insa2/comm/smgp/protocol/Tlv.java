package com.huawei.insa2.comm.smgp.protocol;

import com.huawei.insa2.util.TypeConvert;

public class Tlv {
	public int Tag;
	public int Length;
	public String Value;
	public byte[] TlvBuf;

	public Tlv(int tag, String value) {
		this.Tag = tag;
		this.Length = value.length();
		this.Value = value;
        //System.out.println("tag:"+tag+" value:"+value);
		//add by zyq at 20160501
		//除以下各项，其它TLV 的值均为短整数
		// Mserviceid     业务代码（用于移动网业务)
	    // MsgSrc         信息内容的来源 
		// SrcTermPseudo  短消息发送方的伪码
		// DestTermPseudo 短消息接收方的伪码，当有多个时，长度一致
		// ChargeTermPseudo 计费用户的伪码
		// LinkID         交易标识，用于唯一标识一次交易  20字节串  
		if (tag == TlvId.Mserviceid || tag == TlvId.MsgSrc
				|| tag == TlvId.SrcTermPseudo || tag == TlvId.DestTermPseudo
				|| tag == TlvId.ChargeTermPseudo || tag == TlvId.LinkID ) {
			//参数为oct string
			this.TlvBuf = new byte[4 + this.Length];
			TypeConvert.int2byte2(Tag, TlvBuf, 0);
			TypeConvert.int2byte2(this.Length, TlvBuf, 2);
			System.arraycopy(this.Value.getBytes(), 0, TlvBuf, 4, this.Length);
			//System.out.println("tlv:" + Hex.rhex(TlvBuf));
		} else {
			this.TlvBuf = new byte[4 + 1];
			TypeConvert.int2byte2(Tag, TlvBuf, 0);
			TypeConvert.int2byte2(1, TlvBuf, 2);
			TypeConvert.int2byte3(Integer.parseInt(value), TlvBuf, 4);
			//System.out.println("tlv:" + Hex.rhex(TlvBuf));

		}

	}
}

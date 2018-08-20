package com.huawei.insa2.comm.smgp.protocol;

public class TlvId {
	//add by zyq at 20160429
	public static int TP_pid = 0x0001;    			//GSM协议类型       
	public static int TP_udhi = 0x0002;				//GSM协议类型     
	public static int LinkID = 0x0003;				//交易标识，用于唯一标识一次交易  20字节串
	public static int ChargeUserType = 0x0004;		//计费用户类型 0：对短消息接收方计费 1：对短消息发送方计费 2：对SP计费  
	public static int ChargeTermType = 0x0005;		//计费用户的号码类型 0：真实号码  1：伪码
	public static int ChargeTermPseudo = 0x0006;	//计费用户的伪码
	public static int DestTermType = 0x0007;		//接收方用户类型 0：真实号码  1：伪码
	public static int DestTermPseudo = 0x0008;		//短消息接收方的伪码，当有多个时，长度一致
	public static int PkTotal = 0x0009;				//相同Msg_id 的消息总条数
	public static int PkNumber = 0x000A;			//相同Msg_id 的消息序号，自1开始
	public static int SubmitMsgType = 0x000B;		//SP 发送的消息类型0：普通短消息 1：web定制结果2：web取消定制结果3：终端定制结果4：终端取消定制结果5：包月扣费通知6：web方式定制二次确认
	                                                //7:web方式取消定制二次确认 8：终端方式定制二次确认 9：终端方式取消定制二次确认10：web方式点播二次确认 11：终端方式点播二次确认12：群发
	                                                //13:订购关系同步 14：群发结果通知
	public static int SPDealReslt = 0x000C;			//sp处理结果
	public static int SrcTermType = 0x000D;			//短消息发送方的号码类型
	public static int SrcTermPseudo = 0x000E;		//短消息发送方的伪码
	public static int NodesCount = 0x000F;			//经过的网关数量	
	public static int MsgSrc = 0x0010;				//信息内容的来源
	public static int SrcType = 0x0011;				//传递给SP的源号码类型
	public static int Mserviceid = 0x0012;			//业务代码（用于移动网业务)

}

package com.huawei.insa2.comm.cmpp;

import com.huawei.insa2.util.Resource;

public class CMPPConstant {
	public static boolean debug;

	public static String LOGINING;

	public static String LOGIN_ERROR;

	public static String SEND_ERROR;

	public static String CONNECT_TIMEOUT;

	public static String STRUCTURE_ERROR;

	public static String NONLICETSP_ID;

	public static String SP_ERROR;

	public static String VERSION_ERROR;

	public static String OTHER_ERROR;

	public static String HEARTBEAT_ABNORMITY;

	public static String SUBMIT_INPUT_ERROR;

	public static String CONNECT_INPUT_ERROR;

	public static String CANCEL_INPUT_ERROR;

	public static String QUERY_INPUT_ERROR;

	public static String DELIVER_REPINPUT_ERROR;

	public static String ACTIVE_REPINPUT_ERROR;

	public static String SMC_MESSAGE_ERROR;

	public static String INT_SCOPE_ERROR;

	public static String STRING_LENGTH_GREAT;

	public static String STRING_NULL;

	public static String VALUE_ERROR;

	public static String FEE_USERTYPE_ERROR;

	public static String REGISTERED_DELIVERY_ERROR;

	public static String PK_TOTAL_ERROR;

	public static String PK_NUMBER_ERROR;

	/**请求连接**/
	public static final int Connect_Command_Id = 1;						
	/**请求连接应答**/
	public static final int Connect_Rep_Command_Id = 0x80000001;		
	/**终止连接**/
	public static final int Terminate_Command_Id = 2;					
	/**终止连接应答**/
	public static final int Terminate_Rep_Command_Id = 0x80000002;		
	/**提交短信**/
	public static final int Submit_Command_Id = 4;						
	/**提交短信应答**/
	public static final int Submit_Rep_Command_Id = 0x80000004;			
	/**短信下发**/
	public static final int Deliver_Command_Id = 5;						
	/**下发短信应答**/
	public static final int Deliver_Rep_Command_Id = 0x80000005;		
	/**发送短信状态查询**/
	public static final int Query_Command_Id = 6;						
	/**发送短信状态查询应答**/
	public static final int Query_Rep_Command_Id = 0x80000006;			
	/**删除短信**/
	public static final int Cancel_Command_Id = 7;						
	/**删除短信应答**/
	public static final int Cancel_Rep_Command_Id = 0x80000007;			
	/**激活测试**/
	public static final int Active_Test_Command_Id = 8;					
	/**激活测试应答**/
	public static final int Active_Test_Rep_Command_Id = 0x80000008;	

	
	public CMPPConstant() {
	}

	public static void initConstant(Resource resource) {
		if (LOGINING == null) {
			LOGINING = resource.get("smproxy/logining");
			LOGIN_ERROR = resource.get("smproxy/login-error");
			SEND_ERROR = resource.get("smproxy/send-error");
			CONNECT_TIMEOUT = resource.get("smproxy/connect-timeout");
			STRUCTURE_ERROR = resource.get("smproxy/structure-error");
			NONLICETSP_ID = resource.get("smproxy/nonlicetsp-id");
			SP_ERROR = resource.get("smproxy/sp-error");
			VERSION_ERROR = resource.get("smproxy/version-error");
			OTHER_ERROR = resource.get("smproxy/other-error");
			HEARTBEAT_ABNORMITY = resource.get("smproxy/heartbeat-abnormity");
			SUBMIT_INPUT_ERROR = resource.get("smproxy/submit-input-error");
			CONNECT_INPUT_ERROR = resource.get("smproxy/connect-input-error");
			CANCEL_INPUT_ERROR = resource.get("smproxy/cancel-input-error");
			QUERY_INPUT_ERROR = resource.get("smproxy/query-input-error");
			DELIVER_REPINPUT_ERROR = resource
					.get("smproxy/deliver-repinput-error");
			ACTIVE_REPINPUT_ERROR = resource
					.get("smproxy/active-repinput-error");
			SMC_MESSAGE_ERROR = resource.get("smproxy/smc-message-error");
			INT_SCOPE_ERROR = resource.get("smproxy/int-scope-error");
			STRING_LENGTH_GREAT = resource.get("smproxy/string-length-great");
			STRING_NULL = resource.get("smproxy/string-null");
			VALUE_ERROR = resource.get("smproxy/value-error");
			FEE_USERTYPE_ERROR = resource.get("smproxy/fee-usertype-error");
			REGISTERED_DELIVERY_ERROR = resource
					.get("smproxy/registered-delivery-erro");
			PK_TOTAL_ERROR = resource.get("smproxy/pk-total-error");
			PK_NUMBER_ERROR = resource.get("smproxy/pk-number-error");
		}
	}

	//add by zyq at 20160426
	public static void initConstant() {
		if (LOGINING == null) {
			LOGINING = "正在登录"; //resource.get("smproxy/logining");
			LOGIN_ERROR ="登录过程出现异常：";// resource.get("smproxy/login-error");
			SEND_ERROR = "消息发送有误"; //resource.get("smproxy/send-error");
			CONNECT_TIMEOUT ="登录不成功"; //resource.get("smproxy/connect-timeout");
			STRUCTURE_ERROR = "登录不成功:登录消息结构错"; //resource.get("smproxy/structure-error");
			NONLICETSP_ID = "登录不成功:非法SP_ID"; // resource.get("smproxy/nonlicetsp-id");
			SP_ERROR = "登录不成功:SP认证错"; // resource.get("smproxy/sp-error");
			VERSION_ERROR ="登录不成功:版本太高"; //resource.get("smproxy/version-error");
			OTHER_ERROR ="登录不成功:其它错误"; //resource.get("smproxy/other-error");
			HEARTBEAT_ABNORMITY ="心跳异常(心跳发出多次，没有返回值或返回的值为不成功)"; //resource.get("smproxy/heartbeat-abnormity");
			SUBMIT_INPUT_ERROR = "发送短信消息参数输入有误"; //resource.get("smproxy/submit-input-error");
			CONNECT_INPUT_ERROR = "登录消息参数输入有误";//resource.get("smproxy/connect-input-error");
			CANCEL_INPUT_ERROR = "删除短信请求消息参数输入有误"; //resource.get("smproxy/cancel-input-error");
			QUERY_INPUT_ERROR ="发送短信状态查询请求参数输入有误"; //resource.get("smproxy/query-input-error");
			DELIVER_REPINPUT_ERROR ="对下发的短信响应消息参数定义有误";  //resource.get("smproxy/deliver-repinput-error");
			ACTIVE_REPINPUT_ERROR ="定义响应激活请求的消息参数输入有误"; //resource.get("smproxy/active-repinput-error");
			SMC_MESSAGE_ERROR ="SMC发送过来的消息错误";  //resource.get("smproxy/smc-message-error");
			INT_SCOPE_ERROR ="小于0或大于255"; //resource.get("smproxy/int-scope-error");
			STRING_LENGTH_GREAT = "长度大于"; //resource.get("smproxy/string-length-great");
			STRING_NULL ="值为空"; //resource.get("smproxy/string-null");
			VALUE_ERROR ="不等于0或1"; //resource.get("smproxy/value-error");
			FEE_USERTYPE_ERROR ="fee_UserType的值小于0或大于3"; //resource.get("smproxy/fee-usertype-error");
			REGISTERED_DELIVERY_ERROR ="registered_Delivery的值小于0或大于2"; //resource.get("smproxy/registered-delivery-erro");
			PK_TOTAL_ERROR ="pk_Total的值小于1或大于255";  //resource.get("smproxy/pk-total-error");
			PK_NUMBER_ERROR ="pk_Number的值小于1或大于255"; //resource.get("smproxy/pk-number-error");
		}
	}
}

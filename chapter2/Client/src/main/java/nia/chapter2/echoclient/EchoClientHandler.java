package nia.chapter2.echoclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.3 ChannelHandler for the client
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable						// 标注的实例可以被多个Channel共享
public class EchoClientHandler
    extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {			// 到服务器的链接被建立后被调用
	    System.out.println("this is abc！");   
    	
    	
    	for(int s=0; s<10; s++) {
    		
    		String gconent = "这就是一个测试短信，wgy，cmpp 测试";
    		int size = 1;
    		byte[] bytes = null;
    		try {
    			bytes = gconent.getBytes("iso-10646-ucs-2");
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}
    		for(int i=1;i<=size;i++){
    				byte[] header={
    						0x5,
    						0x8,
    						0x4,
    						0x0,
    						(byte)size,
    						(byte)i
    				};
    				
    				int from = 67*2*(i-1);
    				int length = 67*2;
    				if ((from+length)>bytes.length) {
    					length = bytes.length%(67*2);
    				}
    	
    				byte[] oneMessageBytes=new byte[length+header.length];
    				System.arraycopy(header, 0, oneMessageBytes, 0, header.length);
    				System.arraycopy(bytes, from, oneMessageBytes, header.length, length);
    				
    				try {
    					System.out.println(new String(oneMessageBytes,"iso-10646-ucs-2"));
    				} catch (UnsupportedEncodingException e1) {
    					e1.printStackTrace();
    				}
    			String[] mobile = new String[1];
    			mobile[0] = "15210452774";
    			
    	        /*CMPPSubmitMessage cmppSubmit = new CMPPSubmitMessage(
    	        		1, //pk_Total
    	        		1, //pk_Number
    	                1, //registered_Delivery
    	                06,//msg_Level
    	                "1234567", //service_Id
    	                2, //fee_UserType
    	                "", //fee_Terminal_Id
    	                0,//tp_Pid
    	                1,//tp_Udhi
    	                8,//msg_Fmt
    	                "123",//msg_Src
    	                "01", //fee_Type
    	                "00", //fee_Code
    	                null, //valid_Time
    	                null, //at_Time
    	                "1233213",//src_Terminal_Id
    	                mobile, //destTermId
    	                oneMessageBytes, //msgContent
    	                "" //reserve
    	        );
    	        System.out.println(cmppSubmit.getMsgLength());
    	        try {
    				ctx.writeAndFlush(cmppSubmit);
    	    		try {
    					Thread.sleep(1000);
    				} catch (InterruptedException e) {
    					System.out.println("休眠失败了！");
    				}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}*/
    		}
 
    		
    	}
    	
    	
    	
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {	// 从服务器接受一条消息时被调用
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,				// 异常时被调用
        Throwable cause) {
    	System.out.println("报错啦！");
        cause.printStackTrace();
        ctx.close();
    }
    
    
}

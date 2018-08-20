package nia.chapter2.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.1 EchoServerHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable		// 标注一个ChannelHander可以被多个Channel安全的共享      ChannelInboundHandlerAdapter-ChannelHandlerAdapter：isSharable()    返回{@代码真}，如果实现为{@链接可共享}，那么可以添加到不同的{@链接通道管道}。
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {	// 每个传入的消息都要调用
        ByteBuf in = (ByteBuf) msg;
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8));	// 打印信息到控制台
        ctx.write(in+"_abc");													// 将接收到消息写给发送者，而不冲刷出站消息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)			// 通知 ChannelInboundHandler 最后一次对channelRead()的调用时当前批量读取的最后一条
            throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);				// 将暂存消息冲刷到远程节点，并关闭Channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,				// 读取期间，有异常时调用
        Throwable cause) {
        cause.printStackTrace();										// 打印异常栈跟踪
        ctx.close();													// 关闭Channel
    }
}

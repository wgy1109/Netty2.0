package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * Listing 8.7 Using attributes
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class BootstrapClientWithOptionsAndAttrs {

    /**
     * Listing 8.7 Using attributes
     * */
    public void bootstrap() {
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(
                new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx)
                        throws Exception {
                        Integer idValue = ctx.channel().attr(id).get();   // 使用AttributeKey检索属性以及它的值
                        // do something with the idValue
                    }

                    @Override
                    protected void channelRead0(
                        ChannelHandlerContext channelHandlerContext,
                        ByteBuf byteBuf) throws Exception {
                        System.out.println("Received data");
                    }
                }
            );
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);  // 设置ChannelOption其将在connect()或者bind()方法被调用时被设置到已经创建的Channel上
        bootstrap.attr(id, 123456);								  // 存储该id属性
        ChannelFuture future = bootstrap.connect(
            new InetSocketAddress("www.manning.com", 80));		  // 使用配置好的Bootstrap 实例连接到远程主机 
        future.syncUninterruptibly();
    }
}

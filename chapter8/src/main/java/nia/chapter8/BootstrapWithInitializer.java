package nia.chapter8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.net.InetSocketAddress;

/**
 * Listing 8.6 Bootstrapping and using ChannelInitializer
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class BootstrapWithInitializer {

    /**
     * Listing 8.6 Bootstrapping and using ChannelInitializer
     * */
    public void bootstrap() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())   // 设置EventLoopGroup，其将提供用以处理Channel事件的EventLoop 
            .channel(NioServerSocketChannel.class)	
            .childHandler(new ChannelInitializerImpl());					// 注册一个ChannelInitializerImpl 实例来设置ChannelPipeline 
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080)); // 绑定地址
        future.sync();
    }

    final class ChannelInitializerImpl extends ChannelInitializer<Channel> { // 用以设置ChannelPipeline的自定义ChannelInitializerImpl 实现
        @Override
        protected void initChannel(Channel ch) throws Exception {			 // 将所需的ChannelHandler绑定到ChannelPipeline
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec());
            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

        }
    }
}

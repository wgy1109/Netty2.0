package nia.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Listing 11.1 Adding SSL/TLS support
 * 添加 SSL/TLS 支持
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    public SslChannelInitializer(SslContext context,			// 传入要使用的 SslContext 
        boolean startTls) {										// 如果为 true，第一个写入的消息将不会被加密 （客户端应该设置为true）
        this.context = context;
        this.startTls = startTls;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {		
        SSLEngine engine = context.newEngine(ch.alloc());		// 对于每个SsLHandler实例，都是用Channel的ByteBufAllocator 从SSLContext 获取新的SslEngine  
        ch.pipeline().addFirst("ssl",							
            new SslHandler(engine, startTls));					// 将SSLHandler 作为第一个ChannelHandler添加到ChannelPipeline 中  
    }
}

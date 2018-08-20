package nia.chapter1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by kerr.
 *
 * Listing 1.3 Asynchronous connect
 *
 * Listing 1.4 Callback in action
 * 回调实战
 */
public class ConnectExample {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * Listing 1.3 Asynchronous connect
     *
     * Listing 1.4 Callback in action
     * */
    public static void connect() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; //reference form somewhere
        // Does not block
        ChannelFuture future = channel.connect(
                new InetSocketAddress("192.168.0.1", 25));
        future.addListener(new ChannelFutureListener() {			// 注册一个ChannelFutureListener以便在操作完成时获得通知
            @Override
            public void operationComplete(ChannelFuture future) {	// 检查操作状态
                if (future.isSuccess()) {
                    ByteBuf buffer = Unpooled.copiedBuffer(			// 操作成功，
                            "Hello", Charset.defaultCharset());
                    ChannelFuture wf = future.channel()
                            .writeAndFlush(buffer);
                    // ...
                } else {											// 操作失败
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                }
            }
        });

    }
}
package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * Listing 10.4 TooLongFrameException
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */

public class SafeByteToMessageDecoder extends ByteToMessageDecoder {    // 将字节解码为消息
    private static final int MAX_FRAME_SIZE = 1024;
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
        List<Object> out) throws Exception {
            int readable = in.readableBytes();
            if (readable > MAX_FRAME_SIZE) {							// 检查缓冲区中是否有超过 MAX_FRAME_SIZE 的字节
                in.skipBytes(readable);
                throw new TooLongFrameException("Frame too big!");		// 跳过所有可读字节并抛出 TooLongFrameException 
        }
        // do something
        // ...
    }
}

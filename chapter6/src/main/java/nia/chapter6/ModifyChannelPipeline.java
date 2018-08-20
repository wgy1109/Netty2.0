package nia.chapter6;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import static io.netty.channel.DummyChannelPipeline.DUMMY_INSTANCE;

/**
 * Listing 6.5 Modify the ChannelPipeline
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ModifyChannelPipeline {
    private static final ChannelPipeline CHANNEL_PIPELINE_FROM_SOMEWHERE = DUMMY_INSTANCE;

    /**
     * Listing 6.5 Modify the ChannelPipeline
     * */
    public static void modifyPipeline() {
        ChannelPipeline pipeline = CHANNEL_PIPELINE_FROM_SOMEWHERE; // get reference to pipeline;
        FirstHandler firstHandler = new FirstHandler();  			// 创建 FirstHandler 实例
        pipeline.addLast("handler1", firstHandler);					// 添加到 ChannelPipeline 中
        pipeline.addFirst("handler2", new SecondHandler());
        pipeline.addLast("handler3", new ThirdHandler());
        //...
        pipeline.remove("handler3");								// 通过名称移除“handler3”
        pipeline.remove(firstHandler);								// 通过引用移除 firstHandler 
        pipeline.replace("handler2", "handler4", new FourthHandler());	// 将 “handler2”替换为 FourthHandler “handler4”

    }

    private static final class FirstHandler
        extends ChannelHandlerAdapter {

    }

    private static final class SecondHandler
        extends ChannelHandlerAdapter {

    }

    private static final class ThirdHandler
        extends ChannelHandlerAdapter {

    }

    private static final class FourthHandler
        extends ChannelHandlerAdapter {

    }
}

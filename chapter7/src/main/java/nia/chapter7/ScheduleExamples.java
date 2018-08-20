package nia.chapter7;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Listing 7.2 Scheduling a task with a ScheduledExecutorService
 *
 * Listing 7.3 Scheduling a task with EventLoop
 *
 * Listing 7.4 Scheduling a recurring task with EventLoop
 *
 * Listing 7.5 Canceling a task using ScheduledFuture
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ScheduleExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * Listing 7.2 Scheduling a task with a ScheduledExecutorService
     * */
    public static void schedule() {
        ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(10); // 创建一个线程池，具有10个线程的ScheduledExecutorService

        ScheduledFuture<?> future = executor.schedule(
            new Runnable() {						  // 创建一个Runnable，以供调度稍后执行
            @Override
            public void run() {
                System.out.println("Now it is 60 seconds later");
            }
        }, 60, TimeUnit.SECONDS);					  // 调度任务从现在开始的60秒之后执行
        //...
        executor.shutdown();						  // 调度任务执行完关闭ScheduledExecutorService释放资源
    }

    /**
     * Listing 7.3 Scheduling a task with EventLoop
     * */
    public static void scheduleViaEventLoop() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere	
        ScheduledFuture<?> future = ch.eventLoop().schedule(
            new Runnable() {						  // 创建一个Runnable，以供调度稍后执行
            @Override
            public void run() {
                System.out.println("60 seconds later");
            }
        }, 60, TimeUnit.SECONDS);
    }

    /**
     * Listing 7.4 Scheduling a recurring task with EventLoop
     * */
    public static void scheduleFixedViaEventLoop() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ScheduledFuture<?> future = ch.eventLoop().scheduleAtFixedRate(   
           new Runnable() {
           @Override
           public void run() {
               System.out.println("Run every 60 seconds");
               }
           }, 60, 60, TimeUnit.SECONDS);		// 调度在60秒后执行，并且每隔60秒执行一次
    }

    /**
     * Listing 7.5 Canceling a task using ScheduledFuture
     * */
    public static void cancelingTaskUsingScheduledFuture(){
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ScheduledFuture<?> future = ch.eventLoop().scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Run every 60 seconds");
                    }
                }, 60, 60, TimeUnit.SECONDS);
        // Some other code that runs...
        boolean mayInterruptIfRunning = false;
        future.cancel(mayInterruptIfRunning);   // 取消任务，防止再次运行 
    }
}

package nia.chapter13;

import java.net.InetSocketAddress;

/**
 * Listing 13.1 LogEvent message
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public final class LogEvent {
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    public LogEvent(String logfile, String msg) {	// 用于传出消息的构造函数
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received,	// 用于传入消息的构造函数
        String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {		// 返回发送LogEvent的源InetSocketAddress
        return source;
    }

    public String getLogfile() { 				// 返回所发送的LogEvent的日志文件的名称
        return logfile;
    }

    public String getMsg() {					// 返回消息内容
        return msg;
    }

    public long getReceivedTimestamp() {		// 返回接受LogEvent的时间 
        return received;
    }
}

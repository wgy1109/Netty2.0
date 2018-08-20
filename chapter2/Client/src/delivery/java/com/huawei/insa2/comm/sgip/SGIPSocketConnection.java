// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SGIPSocketConnection.java

package com.huawei.insa2.comm.sgip;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.util.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SGIPSocketConnection extends PLayer
{
	private static final Logger log = LoggerFactory.getLogger(SGIPSocketConnection.class);
	
	private boolean sendTimeOut = false;
	
	private int sendInterval = 0;
	
	protected long disConnectTime = 0;
	
	protected boolean hasConnected = false;
	
	private AtomicInteger window = new AtomicInteger(0); 
	
	public void onWaitWindow(){
		window.incrementAndGet();
	}
	
	public void onReleaseWindow(){
		window.decrementAndGet();
	}
	
	public void setWindowValue(int value){
		window.set(value);
	}
	
    public SGIPSocketConnection(Args args)
    {
        super(null);
        errorTime = new Date();
        port = -1;
        localPort = -1;
        init(args);
        
    }

    protected SGIPSocketConnection()
    {
        super(null);
        errorTime = new Date();
        port = -1;
        localPort = -1;
    }
    protected boolean canSend;
    
    protected void init(Args args)
    {
    	isManStop = false;
        initResource();
        
        error = NOT_INIT;
        setAttributes(args);
        class ReceiveThread extends WatchThread
        {

            public void task()
            {
                try
                {
                	if (error == null && in != null) {
                        PMessage m = in.read();
                        if(m != null){
                        	sendTimeOut = false;
                            onReceive(m);
                        }
                    } 
                	else{
                        if(error != SGIPSocketConnection.NOT_INIT){
                            try
                            {
                                if(reconnectInterval == 0){
                                   Thread.sleep(100);
                                }
                                else{
                            	   Thread.sleep(reconnectInterval);
                                }
                            }
                            catch(InterruptedException interruptedexception) { }
                        }
                        try{
							if(!isManStop){
							    connect();
							    if(error != null){
							    	log.error("host:{},SGIPSocketConnection connect failed:{}",host,error);
							    }
							    
							}else{
								kill();
							}
						}catch(Exception e){
							log.error("host:{},SGIPSocketConnection connect exception: {}",host,e);
						}
                    }
                }
                catch(IOException ioexception) {
                	log.error("host:" + host +",SGIPSocketConnection ioexception:",ioexception);
                	setError(explain(ioexception));
                	log.error("host:{},SGIPSocketConnection ioexception,error:{}",host,error);
                	log.error("host:{},SGIPSocketConnection 当前等待 Response 个数 ：{},ioexception: {}",host,window.get(), ioexception.getLocalizedMessage());
                	if(ioexception instanceof SocketTimeoutException){
                		if(window.get()>5){
                	    	disConnectTime = System.currentTimeMillis();
                			
    	                	try {
    							Thread.sleep(500);
    						} catch (InterruptedException e) {
    						}
    						try{
    							if(!isManStop){
    							    connect();
    							    if(error != null){
    							    	log.error("host:{},SGIPSocketConnection ioexception,connection failed:{}",host,error);
    							    }
    							}
    						   
    						}catch(Exception e){
    							log.error("host:"+host+",SGIPSocketConnection ioexception exception:",e);
    						}
                		}
                		else{
                			setError(null);
                		}
                	}
                	else{
	                	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						try{
							if(!isManStop){
							    connect();
							    if(error != null){
							    	log.error("SGIPSocketConnection not SocketTimeoutException, connection failed:{}",error);
							    }
							}
						   
						}catch(Exception e){
							log.error("host:"+host+",SGIPSocketConnection not SocketTimeoutException, exception:", e);
						}
                	}
                }
                catch(Exception e){
                	log.error("ReceiveThread thread error exception:",e);
                	setError(explain(e));
                	log.error("ReceiveThread thread error:{}",error);
					if(isManStop){
						kill();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
                }
            }

            public ReceiveThread()
            {
                super(String.valueOf(String.valueOf(name)).concat("-receive"));
            }
        }

        receiveThread = new ReceiveThread();
        receiveThread.start();
    }

    protected void init(Args args, Socket socket)
    {
    	isManStop = false;
        initResource();
        error = NOT_INIT;
        if(socket != null)
        {
            this.socket = socket;
            try
            {
                out = getWriter(this.socket.getOutputStream());
                in = getReader(this.socket.getInputStream());
                setError(null);
            }
            catch(IOException ex)
            {
                setError(String.valueOf(CONNECT_ERROR) + String.valueOf(explain(ex)));
                log.error("host:{},SGIPSocket init failed:{}",host,error);
            }
            if(args != null)
                setAttributes1(args);
            class ReceiveThread1 extends WatchThread
            {

                public void task()
                {
                	
                    try
                    {
                        if(error == null && in!=null)
                        {
                            PMessage m = in.read();
                            if(m != null){
                                onReceive(m);
                            }
                        }
                        else{
                        	close();
                        	this.kill();
                        }
                    }
                    catch(IOException ex)
                    {
                        setError(explain(ex));
                        if(ex instanceof SocketTimeoutException){
                        	setError(null);
                            try {
    							Thread.sleep(200);
    						} catch (InterruptedException e1) {
    						}
                        }
                        if(error != null){
                        	log.error("host:{},SGIPSocketConnection ReceiveThread1 IOException:{}",host,error);
                        }
                    }
                    catch(Exception e){
                    	log.error("SGIPSocketConnection ReceiveThread1 Exception:",e);
                    	setError(e.getLocalizedMessage());
                    	log.error("host:{},SGIPSocketConnection ReceiveThread1 error:{}",host,error);
                    }
                }

            public ReceiveThread1()
            {
                super(String.valueOf(String.valueOf(name)).concat("-sgip-server-receive"));
            }
            }

            receiveThread = new ReceiveThread1();
            receiveThread.start();
        }
    }

    protected void onReadTimeOut()
    {
        throw new UnsupportedOperationException("Not implement");
    }
	public void onSendReadTimeOut(){
		sendTimeOut = true;
	}
    public void setAttributes(Args args)
    {
        if(name != null && name.equals(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(String.valueOf(host))))).append(':').append(port)))))
            name = null;
        String oldHost = host;
        int oldPort = port;
        String oldLocalHost = localHost;
        int oldLocalPort = localPort;
        int tps = args.get("msg-submit-tps", -1);
        if(tps!=-1){
        	sendInterval = 1000/tps;
        }
        host = args.get("host", null);
        port = args.get("port", -1);
        localHost = args.get("local-host", null);
        localPort = args.get("local-port", -1);
        name = args.get("name", null);
        if(name == null)
            name = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(host)))).append(':').append(port)));
        readTimeout = 1000 * args.get("read-timeout", 20);
        if(socket != null)
            try
            {
                socket.setSoTimeout(readTimeout);
            }
            catch(SocketException socketexception) { }
        heartbeatInterval = 0;
        transactionTimeout = 1000 * args.get("transaction-timeout", -1);
        if(error == null && host != null && port != -1 && (!host.equals(oldHost) || port != port || !host.equals(oldHost) || port != port))
        {
        	setError("需要重新连接");
            receiveThread.interrupt();
        }
    }

    public void setAttributes1(Args args)
    {
        if(name != null && name.equals(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(String.valueOf(host))))).append(':').append(port)))))
            name = null;
        host = args.get("host", null);
        port = args.get("port", -1);
        localHost = args.get("local-host", null);
        localPort = args.get("local-port", -1);
        name = args.get("name", null);
        if(name == null)
            name = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(host)))).append(':').append(port)));
        readTimeout = 1000 * args.get("read-timeout", 20);
        if(socket != null)
            try
            {
                socket.setSoTimeout(readTimeout);
            }
            catch(SocketException socketexception) { }
        heartbeatInterval = 0;
        transactionTimeout = 1000 * args.get("transaction-timeout", -1);
    }

    public void send(PMessage message)
        throws PException
    {
        if(error != null)
            throw new PException(String.valueOf(SEND_ERROR) + String.valueOf(getError()));
        try
        {
        	if(out==null || in == null || socket ==null){
				throw new IOException();
			}
            out.write(message);
            fireEvent(new PEvent(8, this, message));
        }
        catch(PException ex)
        {
            fireEvent(new PEvent(16, this, message));
            setError(String.valueOf(SEND_ERROR) + String.valueOf(explain(ex)));
            throw ex;
        } catch(SocketException e) {
        	e.printStackTrace();
        	throw new PException("SocketException");
        }
        catch(Exception ex)
        {
            fireEvent(new PEvent(16, this, message));
            setError(String.valueOf(SEND_ERROR) + String.valueOf(explain(ex)));
        }
    }

    public String getName()
    {
        return name;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public int getReconnectInterval()
    {
        return reconnectInterval / 1000;
    }

    public String toString()
    {
        return String.valueOf(String.valueOf((new StringBuffer("PShortConnection:")).append(name).append('(').append(host).append(':').append(port).append(')')));
    }

    public int getReadTimeout()
    {
        return readTimeout / 1000;
    }

    public boolean available()
    {
        return socket!=null && error == null;
    }

    public String getError()
    {
        return error;
    }

    public Date getErrorTime()
    {
        return errorTime;
    }
   
    public synchronized void close()
    {
        try
        {
        	setError(CONNECTION_CLOSED);
        	log.error(conName +" socket close {} ",socket);
        	log.error("host:{} closeing----",host);
            if(socket != null)
            {
                socket.close();
                in = null;
                out = null;
                socket = null;
            }
            
        }
        catch(Exception exception) {log.error("SGIPSocketConnection[close] error:",exception); }
    }
    
    protected synchronized void connect()
    {
    	if(error == NOT_INIT)
            error = CONNECTING;
        else
        if(error == null)
            error = RECONNECTING;
        errorTime = new Date();
        if(socket != null)
            try
            {
                socket.close();
            }
            catch(IOException ioexception) { }
        
        
        try
        {
            if(port <= 0 || port > 65535)
            {
                setError(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(PORT_ERROR)))).append("port:").append(port))));
                return;
            }
            if(localPort < -1 || localPort > 65535)
            {
                setError(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(PORT_ERROR)))).append("local-port:").append(localPort))));
                return;
            }
            if(localHost != null)
            {
                boolean isConnected = false;
                InetAddress localAddr = InetAddress.getByName(localHost);
                if(localPort == -1)
                {
                    for(int p = (int)(Math.random() * (double)64500); p < 0xdc758; p += 13)
                        try
                        {
                            socket = new Socket(host, port, localAddr, 1025 + p % 64500);
                            isConnected = true;
                            break;
                        }
                        catch(IOException ioexception1) { }
                        catch(SecurityException securityexception) { }

                    if(!isConnected)
                        throw new SocketException("Can not find an avaliable local port");
                } else
                {
                    socket = new Socket(host, port, localAddr, localPort);
                }
            } else
            {
                socket = new Socket(host, port);
            }
            socket.setSoTimeout(readTimeout);
            out = getWriter(socket.getOutputStream());
            out.setIntervalTime(sendInterval);
            in = getReader(socket.getInputStream());
            setError(null);
        }
        catch(IOException ex)
        {
            setError(String.valueOf(CONNECT_ERROR) + String.valueOf(explain(ex)));
        }
    }

    protected void setError(String desc)
    {
        if(error == null && desc == null || desc != null && desc.equals(error))
            return;
        error = desc;
        errorTime = new Date();
//        if(desc == null)
//            desc = CONNECTED;
    }

    protected abstract PWriter getWriter(OutputStream outputstream);

    protected abstract PReader getReader(InputStream inputstream);

    protected abstract Resource getResource();

    protected void heartbeat()
        throws IOException
    {
    }

    public void initResource()
    {
        //modify by zyq at 20160426
    	
    	NOT_INIT = "连接尚未初始化。";//resource.get("comm/not-init");
        CONNECTING ="正在连接。";// resource.get("comm/connecting");
        RECONNECTING ="正在重连。";// resource.get("comm/reconnecting");
        CONNECTED ="连接建立成功。";// resource.get("comm/connected");
        HEARTBEATING = "正在发送心跳消息。"; //resource.get("comm/heartbeating");
        RECEIVEING ="正在接收消息。"; //resource.get("comm/receiveing");
        CLOSEING ="正在关闭连接。";//resource.get("comm/closeing");
        CLOSED ="连接正常关闭。"; //resource.get("comm/closed");
        UNKNOWN_HOST ="未知主机。(错误的IP地址或不能根据主机名得到对应的IP地址。)"; //resource.get("comm/unknown-host");
        PORT_ERROR ="端口号错误。(不是0~65535范围内的整数。)"; //resource.get("comm/port-error");
        CONNECT_REFUSE ="连接被对方拒绝。(连接对方指定端口不在监听状态。)"; //resource.get("comm/connect-refused");
        NO_ROUTE_TO_HOST = "没有通向该地址的路由。(地址不存在、对方未开机、路由不通或者物理线路故障"; //resource.get("comm/no-route");
        RECEIVE_TIMEOUT ="接收消息超时。(请确认设置的read-timeout参数大于服务器心跳发送频率的2~3倍，并确认服务器是否正确发出心跳消息。)"; //resource.get("comm/receive-timeout");
        CLOSE_BY_PEER ="连接被对方关闭。(服务器端主动地关闭了我们之间的连接。请检查服务器配置是否正确，是否有接入方地址限制。)"; //resource.get("comm/close-by-peer");
        RESET_BY_PEER ="连接被对方重置。(服务器端非正常关闭了连接，可能是服务进程非正常退出了。例如：call dump或被管理员kill掉。)"; // resource.get("comm/reset-by-peer");
        CONNECTION_CLOSED ="连接已经关闭";//resource.get("comm/connection-closed");
        COMMUNICATION_ERROR ="通信异常。";// resource.get("comm/communication-error");
        CONNECT_ERROR ="建立连接失败："; //resource.get("comm/connect-error");
        SEND_ERROR = "发送消息失败："; //resource.get("comm/send-error");
        RECEIVE_ERROR ="接收消息失败。"; //resource.get("comm/receive-error");
        CLOSE_ERROR ="关闭连接出错。"; //resource.get("comm/close-error");
    }

    protected String explain(Exception ex)
    {
        String msg = ex.getMessage();
        if(msg == null)
            msg = "";
        if(ex instanceof PException)
            return ex.getMessage();
        if(ex instanceof EOFException)
            return CLOSE_BY_PEER;
        if(msg.indexOf("Connection reset by peer") != -1)
            return RESET_BY_PEER;
        if(msg.indexOf("SocketTimeoutException") != -1)
            return RECEIVE_TIMEOUT;
        if(ex instanceof SocketTimeoutException)
            return RECEIVE_TIMEOUT;
        if(ex instanceof NoRouteToHostException)
            return NO_ROUTE_TO_HOST;
        if(ex instanceof ConnectException)
            return CONNECT_REFUSE;
        if(ex instanceof UnknownHostException)
            return UNKNOWN_HOST;
        if(msg.indexOf("errno: 128") != -1)
        {
            return NO_ROUTE_TO_HOST;
        } else
        {
        	//modify by zyq at 20160429
            //ex.printStackTrace();
            return ex.toString();
        }
    }

    protected static String NOT_INIT;
    protected static String CONNECTING;
    protected static String RECONNECTING;
    protected static String CONNECTED;
    protected static String HEARTBEATING;
    protected static String RECEIVEING;
    protected static String CLOSEING;
    protected static String CLOSED;
    protected static String UNKNOWN_HOST;
    protected static String PORT_ERROR;
    protected static String CONNECT_REFUSE;
    protected static String NO_ROUTE_TO_HOST;
    protected static String RECEIVE_TIMEOUT;
    protected static String CLOSE_BY_PEER;
    protected static String RESET_BY_PEER;
    protected static String CONNECTION_CLOSED;
    protected static String COMMUNICATION_ERROR;
    protected static String CONNECT_ERROR;
    protected static String SEND_ERROR;
    protected static String RECEIVE_ERROR;
    protected static String CLOSE_ERROR;
    private String error;
    protected Date errorTime;
    protected String name;
    protected String host;
    protected int port;
    protected String localHost;
    protected int localPort;
    protected int heartbeatInterval;
    protected PReader in;
    protected PWriter out;
    //protected static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    protected int readTimeout;
    protected int reconnectInterval;
    protected Socket socket;
    protected WatchThread heartbeatThread;
    protected WatchThread receiveThread;
    protected int transactionTimeout;
    protected Resource resource;
    protected boolean isManStop = false;  //是否人工停止

	public boolean isCanSend() {
		return canSend;
	}

	public void setCanSend(boolean canSend) {
		this.canSend = canSend;
	}
	
	private String conName;
	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName;
	}

	public long getDisConnectTime() {
		return disConnectTime;
	}

	public void setDisConnectTime(long disConnectTime) {
		this.disConnectTime = disConnectTime;
	}

	public boolean isHasConnected() {
		return hasConnected;
	}

	public void setHasConnected(boolean hasConnected) {
		this.hasConnected = hasConnected;
	}
	
	public int getTps(){
		if(out!=null){
			return out.getTps();
		}
		return 0;
	}
	public void setTps(int tps){
		if(tps>0){
        	sendInterval = 1000/tps;
        }
		else{
			sendInterval = 0;
		}
		if(out!=null){
        	out.setIntervalTime(sendInterval);
        }
	}
}

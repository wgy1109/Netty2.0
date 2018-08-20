package com.huawei.insa2.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PWriter {
	
	protected List<Long> sendList = new ArrayList<Long>();
	
	
	protected int intervalTime = 0;
	
	protected long preSendFinishTime = 0;
	
	public abstract void write(PMessage pmessage) throws IOException;
	
	public synchronized void addSendList(long current){
		filterSendList(current);
		sendList.add(current);
	}
	private synchronized void filterSendList(long current){
		for(int i=0;i<sendList.size();i++){
			Long time = sendList.get(i);
			if(current-time>1000){
				sendList.remove(i);
				i--;
			}
			else{
				break;
			}
		}
	}
	
	public int getTps(){
		filterSendList(System.currentTimeMillis());
		return sendList.size();
	}
	
	public void setIntervalTime(int sendInterval){
		this.intervalTime = sendInterval;
	}
}

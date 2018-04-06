package com.tristan.timertask;

import java.util.TimerTask;

import android.util.Log;

import com.tristan.aalocuser.DataPool;

public class Clock extends TimerTask {

	private DataPool dataPool;
	private int index;   //用于发声的节点的编号，用于时钟同步
	
	public Clock(DataPool dataPool, int index){
		this.dataPool = dataPool;
		this.index = index;
	}
	
	@Override
	public void run() {
		//02作为中心结点发声的标志位，用于时钟同步
		dataPool.getPrintWriters()[index-1].println("02");
		Log.i("init", "I have sent '02'. ");
	}

}

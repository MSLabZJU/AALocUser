package com.tristan.timertask;

import java.util.TimerTask;

import android.util.Log;

import com.tristan.aalocuser.DataPool;

public class Clock extends TimerTask {

	private DataPool dataPool;
	private int index;   //���ڷ����Ľڵ�ı�ţ�����ʱ��ͬ��
	
	public Clock(DataPool dataPool, int index){
		this.dataPool = dataPool;
		this.index = index;
	}
	
	@Override
	public void run() {
		//02��Ϊ���Ľ�㷢���ı�־λ������ʱ��ͬ��
		dataPool.getPrintWriters()[index-1].println("02");
		Log.i("init", "I have sent '02'. ");
	}

}

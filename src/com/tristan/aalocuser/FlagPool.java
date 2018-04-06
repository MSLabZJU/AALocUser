package com.tristan.aalocuser;

import android.R.integer;
import android.util.Log;

/**
 * 
 * @author TristanHuang
 * 2018-2-25  下午8:14:09
 */
public class FlagPool {
	/*
	 * 开始录音的反馈标志位，每次建立连接后每个beacon会发送过来一个"520"信号
	 * 然后这里的@param feedBackStart就会++，5个结点的话就会在TimerTask中判断是否到了5
	 */
	public static int feedBackStart = 0; //开始录音的反馈标志位
	public static boolean isRecord = false; //记录录音状态的标志位
	
	public static boolean readyForNext = true;
	
	/** status for test */
	public static boolean test = false;
	//第一组连接状态
	private static boolean connect1 = false;
	private static boolean connect2 = false;
	private static boolean connect3 = false;
	private static boolean connect4 = false;
	private static boolean connect5 = false;
	//第二组连接状态
	private static boolean connect6 = false;
	private static boolean connect7 = false;
	private static boolean connect8 = false;
	private static boolean connect9 = false;
	private static boolean connect10 = false;
	
	private static boolean compute1 = false;
	private static boolean compute2 = false;
	private static boolean compute3 = false;
	private static boolean compute4 = false;
	private static boolean compute5 = false;
	private static boolean compute6 = false;
	
	//********************构造器区域***********************
	private static FlagPool instance = new FlagPool();
	
	private FlagPool(){
		super();
	}
	
	public static FlagPool getInstance(){
		return instance;
	}
	//*****************************************************
	
	public boolean allConnectOrNot_one(){
		return connect1 && connect2 && connect3 && connect4 && connect5;
	}
	
	public boolean allConnectOrNot_two(){
		return connect6 && connect7 && connect8 && connect9 && connect10;
	}
	
	
	public boolean getConnectCentralBeacon(){
		return connect5;
	}
	
	public boolean getComputeCenralBeacon(){
		return compute5;
	}
	
	public void resetCompute(){
		compute1 = false;
		compute2 = false;
		compute3 = false;
		compute4 = false;
		compute5 = false;
		compute6 = false;
	}
	
	public void setRecordStatus(boolean val){
		isRecord = val;
	}
	
	public boolean allComputeOrNot(){
		return compute1 && compute2 && compute3 && compute4 && compute5 && compute6;
	}
	
	public void setCompute(int index, boolean flag) {
		switch (index) {
		case 1:
			compute1 = flag;
			break;
		case 2:
			compute2 = flag;
			break;
		case 3:
			compute3 = flag;
			break;
		case 4:
			compute4 = flag;
			break;
		case 5:
			compute5 = flag;
			Log.i("init", "index = 5 :" + flag);
			break;
		case 6:
			compute6 = flag;
		default:
			break;
		}
	}
	
	public void setConnect(int index, boolean flag) {
		switch (index) {
		case 1:
			connect1 = flag;
			break;
		case 2:
			connect2 = flag;
			break;
		case 3:
			connect3 = flag;
			break;
		case 4:
			connect4 = flag;
			break;
		case 5:
			connect5 = flag;
			break;
		case 6:
			connect6 = flag;
			break;
		case 7:
			connect7 = flag;
			break;
		case 8:
			connect8 = flag;
			break;
		case 9:
			connect9 = flag;
			break;
		case 10:
			connect10 = flag;
			break;
		default:
			break;
		}
	}
}

package com.tristan.aalocuser;

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
	
	private static boolean connect1 = false;
	private static boolean connect2 = false;
	private static boolean connect3 = false;
	private static boolean connect4 = false;
	private static boolean connect5 = false;
	
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
	
	public boolean allConnectOrNot(){
		return connect1 && connect2 && connect3 && connect4 && connect5;
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
		default:
			break;
		}
	}
}

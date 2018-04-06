package com.tristan.aalocuser;

import android.R.integer;
import android.util.Log;

/**
 * 
 * @author TristanHuang
 * 2018-2-25  ����8:14:09
 */
public class FlagPool {
	/*
	 * ��ʼ¼���ķ�����־λ��ÿ�ν������Ӻ�ÿ��beacon�ᷢ�͹���һ��"520"�ź�
	 * Ȼ�������@param feedBackStart�ͻ�++��5�����Ļ��ͻ���TimerTask���ж��Ƿ���5
	 */
	public static int feedBackStart = 0; //��ʼ¼���ķ�����־λ
	public static boolean isRecord = false; //��¼¼��״̬�ı�־λ
	
	public static boolean readyForNext = true;
	
	/** status for test */
	public static boolean test = false;
	//��һ������״̬
	private static boolean connect1 = false;
	private static boolean connect2 = false;
	private static boolean connect3 = false;
	private static boolean connect4 = false;
	private static boolean connect5 = false;
	//�ڶ�������״̬
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
	
	//********************����������***********************
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

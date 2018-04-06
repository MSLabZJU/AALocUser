package com.tristan.aalocuser;

import android.R.integer;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class ConfigPool {
	//设定中心发声结点的index
	public static int instantTimeIndex = 10;
	public static int currentIndex = 10;
	


	//设置音频来源为麦克风
	public static final int AUDIOSOURCE = MediaRecorder.AudioSource.MIC;
	//设置采样频率
	public static final int FREQUENCY = 44100;
	//设置音频的录制声道:CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	public static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
	//音频数据格式：PCM编码的样本位数，或者8位，或者16位。要保证设备支持
	public static final int AUDIOFORMAT = AudioFormat.ENCODING_PCM_16BIT;
	//缓冲区大小
	public static final int BUFFERSIZE = AudioRecord.getMinBufferSize(FREQUENCY,CHANNEL,AUDIOFORMAT);
	
	//********************构造器区域***********************
	private static ConfigPool instance = new ConfigPool();
	
	private ConfigPool() {
		super();
	}
	
	public static ConfigPool getInstance(){
		Log.i("IO", "缓冲区大小为："+BUFFERSIZE);
		return instance;
	}
	//*****************************************************
	
	public AudioRecord getAudioRecord(){
		return new AudioRecord(AUDIOSOURCE, FREQUENCY, CHANNEL, AUDIOFORMAT, BUFFERSIZE);
	}
	
	public static int getCurrentIndex() {
		return currentIndex;
	}

	public static void setCurrentIndex() {
		ConfigPool.currentIndex = ConfigPool.instantTimeIndex;
	}
	

	public static void setInstantTimeIndex(int instantTimeIndex) {
		ConfigPool.instantTimeIndex = instantTimeIndex;
	}
}

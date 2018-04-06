package com.tristan.aalocuser;

import android.R.integer;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class ConfigPool {
	//�趨���ķ�������index
	public static int instantTimeIndex = 10;
	public static int currentIndex = 10;
	


	//������Ƶ��ԴΪ��˷�
	public static final int AUDIOSOURCE = MediaRecorder.AudioSource.MIC;
	//���ò���Ƶ��
	public static final int FREQUENCY = 44100;
	//������Ƶ��¼������:CHANNEL_IN_STEREOΪ˫������CHANNEL_CONFIGURATION_MONOΪ������
	public static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
	//��Ƶ���ݸ�ʽ��PCM���������λ��������8λ������16λ��Ҫ��֤�豸֧��
	public static final int AUDIOFORMAT = AudioFormat.ENCODING_PCM_16BIT;
	//��������С
	public static final int BUFFERSIZE = AudioRecord.getMinBufferSize(FREQUENCY,CHANNEL,AUDIOFORMAT);
	
	//********************����������***********************
	private static ConfigPool instance = new ConfigPool();
	
	private ConfigPool() {
		super();
	}
	
	public static ConfigPool getInstance(){
		Log.i("IO", "��������СΪ��"+BUFFERSIZE);
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

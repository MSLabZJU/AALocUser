package com.tristan.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioRecord;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.work.AudioRecordImpl;
import com.tristan.work.ComputeImpl;

public class TestRecordStart extends TimerTask {

	private DataPool dataPool;
	private FlagPool flagPool;
	private Button btn;
	private TextView tv_info;
	private TextView tv_result;
	private Timer timer;
	private AudioRecord audioRecord;
	
	public TestRecordStart(DataPool dataPool, FlagPool flagPool, 
			TextView tv_info, TextView tv_result, Button btn, Timer timer, AudioRecord audioRecord){
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.btn = btn;
		this.tv_info = tv_info;
		this.tv_result = tv_result;
		this.timer = timer;
		this.audioRecord = audioRecord;
	}
	
	public void run(){
		while (dataPool.getPrintWriterOnlyOne() == null) {
			
		}
		dataPool.getPrintWriterOnlyOne().println("00");
		
		//用户端开始录音
		new File(dataPool.getFolderName()).mkdirs();
		flagPool.setRecordStatus(true);
		
		startRecord();
		
	}
	
	private void startRecord() {
		audioRecord.startRecording();
		Log.i("init", "start recording");
		flagPool.setRecordStatus(true);
		new Thread(new TestAudioRecordImpl(dataPool, flagPool,tv_info, tv_result, btn, audioRecord)).start();
	}

}

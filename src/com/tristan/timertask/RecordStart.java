package com.tristan.timertask;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioRecord;
import android.widget.Button;

import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.work.AudioRecordImpl;

public class RecordStart extends TimerTask {
	private DataPool dataPool;
	private FlagPool flagPool;
	private Button btn;
	private Timer timer;
	private AudioRecord audioRecord;
	
	public RecordStart(DataPool dataPool, FlagPool flagPool, 
			Button btn, Timer timer, AudioRecord audioRecord){
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.btn = btn;
		this.timer = timer;
		this.audioRecord = audioRecord;
	}
	
	public void run(){
		for (PrintWriter pw : dataPool.getPrintWriters()) {
			//00作为开始录音的标志位
			pw.println("00");
		}
		
		//用户端开始录音
		new File(dataPool.getFolderName()).mkdirs();
		flagPool.setRecordStatus(true);
		
		startRecord();
		
	}
	
	private void startRecord() {
		audioRecord.startRecording();
		flagPool.setRecordStatus(true);
		new Thread(new AudioRecordImpl(dataPool, flagPool, btn, timer,audioRecord)).start();
	}
}

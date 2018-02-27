package com.tristan.timertask;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;

import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.aalocuser.MainActivity;

public class RecordEnd extends TimerTask {

	private MainActivity activity;
	private DataPool dataPool;
	private FlagPool flagPool;
	private Timer timer;
	
	public RecordEnd(Activity activity, DataPool dataPool, FlagPool flagPool, Timer timer){
		this.activity = (MainActivity) activity;
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.timer = timer;
	}
	
	@Override
	public void run() {
		dataPool.sendEndSignal();
		
		//”√ªß∂ÀΩ· ¯¬º“Ù
		flagPool.setRecordStatus(false);
		activity.stopSound();
		timer.purge();
	}

}

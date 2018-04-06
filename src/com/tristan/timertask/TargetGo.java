package com.tristan.timertask;

import java.util.Timer;
import java.util.TimerTask;

import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.aalocuser.MainActivity;

import android.app.Activity;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class TargetGo extends TimerTask {

	private MainActivity activity;
	private DataPool dataPool;
	private FlagPool flagPool;
	private Timer timer;
	
	public TargetGo(MainActivity activity, DataPool dataPool, FlagPool flagPool, Timer timer){
		this.activity = activity;
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.timer = timer;
	}
	
	@Override
	public void run() {
		activity.playSounds(1, 1);
		Log.i("init", "·¢Éù£¡£¡£¡");
		timer.schedule(new RecordEnd(activity, dataPool, flagPool, timer), 300);  //½áÊøÂ¼Òô
	}

}

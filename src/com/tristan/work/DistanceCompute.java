package com.tristan.work;

import java.util.Timer;

import android.media.AudioRecord;
import android.widget.Button;

import com.tristan.aalocuser.ConfigPool;
import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.timertask.Clock;
import com.tristan.timertask.RecordStart;

public class DistanceCompute implements Runnable {

	private DataPool dataPool;
	private FlagPool flagPool;
	private Button btn_sound;
	private Timer timer;
	private AudioRecord audioRecord;
	
	private double[] dists = new double[DataPool.NODENUM] ; 
	private double[] result = new double[3];
	
	public DistanceCompute(DataPool dataPool, FlagPool flagPool, 
			Button btn_sound, Timer timer, AudioRecord audioRecord) {
		super();
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.btn_sound = btn_sound;
		this.timer = timer;
		this.audioRecord = audioRecord;
	}

	public void run() {
		while (true) {
			if (flagPool.allComputeOrNot()) {
				//!!!progressOne
				dists = dataPool.progressOne();
				flagPool.resetCompute();
				
				if (dataPool.isLoopEnd()) {
					//!!!progressTwo
					result = dataPool.progressTwo();
					btn_sound.post(new Runnable() {
						public void run() {
							btn_sound.setEnabled(true);
						}
					});
				}else {
					timer.schedule(new RecordStart(dataPool, flagPool, btn_sound, timer, audioRecord), 0); //开始录音
					long time1 = System.currentTimeMillis();
					while (true) {
						if (flagPool.feedBackStart == 5) {
							timer.schedule(new Clock(dataPool, ConfigPool.INDEX), 400); //开始时钟同步
							flagPool.feedBackStart = 0;
							break;
						}
						long time2 = System.currentTimeMillis();
						if ((time2-time1) > (long)3) {
							timer.schedule(new Clock(dataPool, ConfigPool.INDEX), 400); //开始时钟同步
							flagPool.feedBackStart = 0;
							break;
						}
					}
				}
				
				//将实验数据封装成DataTrans类
				DataTrans dt = new DataTrans();
				dt.SetData(dataPool, dataPool.time);
				dt.Print();
				dt.WriteToText();
				
				break;
			}
		}
	}

}

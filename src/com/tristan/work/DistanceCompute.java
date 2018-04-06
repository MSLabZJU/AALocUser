package com.tristan.work;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Timer;

import android.media.AudioRecord;
import android.widget.Button;
import android.widget.TextView;

import com.tristan.aalocuser.ConfigPool;
import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.timertask.Clock;
import com.tristan.timertask.RecordStart;

public class DistanceCompute implements Runnable {

	private DataPool dataPool;
	private FlagPool flagPool;
	private Button btn_sound;
	private TextView tv_result;
	private Timer timer;
	private AudioRecord audioRecord;
	private OutputStreamWriter pw;
	private DecimalFormat df;
	private String resultString;
	
	private double[] dists = new double[DataPool.NODENUM] ; 
	private double[] result = new double[3];
	
	public DistanceCompute(DataPool dataPool, FlagPool flagPool, 
			Button btn_sound, TextView tv_result, Timer timer, AudioRecord audioRecord) {
		super();
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.btn_sound = btn_sound;
		this.tv_result = tv_result;
		this.timer = timer;
		this.audioRecord = audioRecord;
		df = new DecimalFormat("#.00");
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
					resultString = df.format(result[0])+","+df.format(result[1])+","+df.format(result[2]);
					WriteToText(resultString);
					FlagPool.readyForNext = true;
					tv_result.post(new Runnable() {
						public void run() { 
							tv_result.setText(resultString);
						}
					});
					btn_sound.post(new Runnable() {
						public void run() {
							btn_sound.setEnabled(true);
						}
					});
				}else {
					timer.schedule(new RecordStart(dataPool, flagPool, btn_sound, tv_result, timer, audioRecord), 0); //开始录音
					long time1 = System.currentTimeMillis();
					while (true) {
						if (flagPool.feedBackStart == 5) {
							timer.schedule(new Clock(dataPool, ConfigPool.getIndex()), 400); //开始时钟同步
							flagPool.feedBackStart = 0;
							break;
						}
						long time2 = System.currentTimeMillis();
						if ((time2-time1) > (long)3) {
							timer.schedule(new Clock(dataPool, ConfigPool.getIndex()), 400); //开始时钟同步
							flagPool.feedBackStart = 0;
							break;
						}
					}
				}
				
				//将实验数据封装成DataTrans类
				DataTrans dt = new DataTrans();
				dt.SetData(dataPool, dataPool.time);
//				dt.Print();
//				dt.WriteToText();
				
				
				
				break;
			}
		}
		
	}
	
	public void WriteToText(String resultString) {
		try {
			pw = new OutputStreamWriter(new FileOutputStream("/mnt/sdcard/AALocResult.txt", true), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			pw.write(resultString+","+System.currentTimeMillis()+"\r\n");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

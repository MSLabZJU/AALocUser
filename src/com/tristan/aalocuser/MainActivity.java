package com.tristan.aalocuser;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import com.tristan.connect.ConnectImpl;
import com.tristan.connect.WaitForConnectImpl;
import com.tristan.timertask.Clock;
import com.tristan.timertask.RecordStart;

import android.app.Activity;
import android.graphics.Bitmap.Config;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * 计划：
 *     1. 完成User与Beacon之间的测距功能
 *     2. 添加任意多结点功能
 * @author TristanHuang
 * 2018-2-25  下午3:13:26
 */
public class MainActivity extends Activity {
	
	/** info of connectivity and status.*/
	private TextView tv_detail;
	/** btns.*/
	private Button btn_connect;
	private Button btn_import;
	private Button btn_sound;
	/** static data.*/
	private DataPool dataPool;
	private FlagPool flagPool;
	private ConfigPool configPool;
	/** timer */
	private Timer timer;
	/** make sound */
	private AudioRecord audioRecord;
	private static SoundPool soundPool;
	private HashMap<Integer, Integer> spMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findGuys();
		
		initViews();
		
		setListener();
	}

	private void findGuys() {
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		/** btns. */
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_import = (Button) findViewById(R.id.btn_import);
		btn_sound = (Button) findViewById(R.id.btn_sound);
		/** static data. */
		dataPool = DataPool.getInstance();
		flagPool = FlagPool.getInstance();
		configPool = ConfigPool.getInstance();
		/** create the folder for wavfile */
		while (new File(dataPool.getFolderName()).exists()) {
			dataPool.FolderNum++;
		}
		/** timer */
		timer = new Timer();
		/** objects for making sound. */
		audioRecord = configPool.getAudioRecord();
		soundPool = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);// 同时播放的最大音频数为7
		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, soundPool.load(this, R.raw.data_refer, 1));
	}
	
	private void initViews() {
		// keep the screen on
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
		// set the scrolling-movement function of tv_detail.
		tv_detail.setMovementMethod(ScrollingMovementMethod.getInstance());
		// set the status of btns.
		btn_connect.setEnabled(true);
		btn_import.setEnabled(false);
		btn_sound.setEnabled(false);
	}

	private void setListener() {
		btn_connect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// change the status of btns
				btn_connect.setEnabled(false);
				btn_import.setEnabled(true);
				new Thread(new ConnectImpl(MainActivity.this, 2001, 1, tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2002, 2, tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2003, 3, tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2004, 4, tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2005, 5, tv_detail, dataPool, flagPool, timer)).start();
			}
		});
		
		btn_import.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				DataPool.setDistance(10, 10, 10, 10);
				new Thread(new WaitForConnectImpl(flagPool, btn_import, btn_sound)).start();
			}
		});
		
		btn_sound.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				btn_sound.setEnabled(false);
				//开始录音
				timer.schedule(new RecordStart(dataPool, flagPool, btn_sound, timer, audioRecord), 0);
				long time1 = System.currentTimeMillis();
				while (true) {
					if (flagPool.feedBackStart == 5) {
						timer.schedule(new Clock(dataPool, ConfigPool.INDEX), 400);
						flagPool.feedBackStart = 0;
						break;
					}
					long time2 = System.currentTimeMillis();
					if ((time2-time1) > (long)3) {
						timer.schedule(new Clock(dataPool, ConfigPool.INDEX), 400);
						flagPool.feedBackStart = 0;
						break;
					}
				}
			}
		});
	}
	
	/**
	 * @param sound   spMap中信号的序号
	 * @param number  循环的次数
	 */
	public void playSounds(int sound, int number) {

		// AudioManger对象通过getSystemService(Service.AUDIO_SERVICE)获取
		AudioManager am = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
		// 获得手机播放最大音乐音量
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// float audioCurrentVolumn =
		// am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioMaxVolumn;
		soundPool.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number, 1);
	}
	
	public void stopSound(){
		if (audioRecord != null) {
			Log.i("IO", "stop Recording...");
			flagPool.setRecordStatus(false);
			audioRecord.stop();
		}
	}
}

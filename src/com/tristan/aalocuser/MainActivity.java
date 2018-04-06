package com.tristan.aalocuser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tristan.connect.ConnectImpl;
import com.tristan.connect.WaitForConnectImpl;
import com.tristan.sensor.PressureListener;
import com.tristan.test.TestRecordStart;
import com.tristan.timertask.Clock;
import com.tristan.timertask.RecordStart;
import com.tristan.timertask.ResetBeacons;
import com.tristan.view.StatusTextView;

/**
 * 计划：
 *     1. 完成User与Beacon之间的测距功能
 *     2. 添加任意多结点功能
 * @author TristanHuang
 * 2018-2-25  下午3:13:26
 */
public class MainActivity extends Activity {
	
	/** tvs.*/
	private LinearLayout ll_connect;
	/** tvs.*/
	private StatusTextView[] tvs_connect;
	private TextView tv_pressure;
	private TextView tv_layer;
	private TextView tv_detail;
	private TextView tv_result;
	private TextView tv_ip;
	/** ets. */
	private EditText et_pressure_1;
	private EditText et_pressure_2;
	private EditText et_5_1;
	private EditText et_5_2;
	private EditText et_5_3;
	private EditText et_5_4;
	private EditText et_10_6;
	private EditText et_10_7;
	private EditText et_10_8;
	private EditText et_10_9;
	/** btns.*/
	private CheckBox cb_datalock;
	private Button btn_reset;
	private Button btn_test;
	private Button btn_connect;
	private Button btn_import;
	private Button btn_sound;
	/** static data.*/
	private int screen_x;
	private int screen_y;
	/** static data.*/
	private DataPool dataPool;
	private FlagPool flagPool;
	private ConfigPool configPool;
	/** timer */
	private Timer timer;
	/** make sound */
	private AudioRecord audioRecord;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> spMap;
	/** status of the user */
	private String ipAddress;
	/** sensors */
	private SensorManager sensorManager;
	private PressureListener pressureListener;
	private Sensor preSensor;
	private int frequency;
	/** datalog */
	private OutputStreamWriter pw;
	/** save the preference */
	private SharedPreferences pressures;
	private SharedPreferences distances;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findGuys();
		
		initViews();
		
		setListener();
	}

	private void findGuys() {
		screen_x = getWindowManager().getDefaultDisplay().getWidth();//获取屏幕宽度
		screen_y = getWindowManager().getDefaultDisplay().getHeight();//获取屏幕高度
		/** views. */
		ll_connect = (LinearLayout) findViewById(R.id.ll_connect);
		tvs_connect = new StatusTextView[10];
		initialConnectStatusViews(ll_connect, tvs_connect);
		/** tvs. */
		tv_pressure = (TextView) findViewById(R.id.tv_pressure);
		tv_layer = (TextView) findViewById(R.id.tv_layer);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_result = (TextView) findViewById(R.id.tv_result);
		tv_ip = (TextView) findViewById(R.id.tv_ip);
		/** ets. */
		et_pressure_1 = (EditText) findViewById(R.id.et_pressure_1st);
		et_pressure_2 = (EditText) findViewById(R.id.et_pressure_2nd);
		et_5_1 = (EditText) findViewById(R.id.dt_dis_5_1);
		et_5_2 = (EditText) findViewById(R.id.dt_dis_5_2);
		et_5_3 = (EditText) findViewById(R.id.dt_dis_5_3);
		et_5_4 = (EditText) findViewById(R.id.dt_dis_5_4);
		et_10_6 = (EditText) findViewById(R.id.dt_dis_10_6);
		et_10_7 = (EditText) findViewById(R.id.dt_dis_10_7);
		et_10_8 = (EditText) findViewById(R.id.dt_dis_10_8);
		et_10_9 = (EditText) findViewById(R.id.dt_dis_10_9);
		pressures = this.getSharedPreferences("pressure", MODE_PRIVATE);
		distances = this.getSharedPreferences("distance", MODE_PRIVATE);
		/** btns. */
		cb_datalock = (CheckBox) findViewById(R.id.cb_datalock);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_test = (Button) findViewById(R.id.btn_test);
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_import = (Button) findViewById(R.id.btn_import);
		btn_sound = (Button) findViewById(R.id.btn_sound);
		/** static data. */
		dataPool = DataPool.getInstance();
		flagPool = FlagPool.getInstance();
		configPool = ConfigPool.getInstance();
		/** sensor */
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		pressureListener = new PressureListener(tv_pressure, tv_layer);
		preSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
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
		
		ipAddress = getIpAddress();
		Log.i("init", "Yes, I have found all of them.");
	}
	
	private void initialConnectStatusViews(LinearLayout layout, StatusTextView[] tvs_connect) {
		for (int i = 0; i < tvs_connect.length; i++) {
			tvs_connect[i] = new StatusTextView(this, i);
			tvs_connect[i].setText(String.valueOf(i+1));
			tvs_connect[i].setWidth(screen_x/10);
			tvs_connect[i].setGravity(Gravity.CENTER);
			layout.addView(tvs_connect[i]);
		}
	}

	private void initViews() {
		// keep the screen on
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
		// set the scrolling-movement function of tv_detail.
		tv_detail.setMovementMethod(ScrollingMovementMethod.getInstance());
		tv_ip.setText("IP Address:  "+ ipAddress);
		// set the status of btns.
		btn_reset.setEnabled(false);
		btn_connect.setEnabled(true);
		btn_import.setEnabled(false);
		btn_sound.setEnabled(false);
		//register the sensor listener
		frequency = 20000 * 5 * 10;  // 1 times 3 second
		sensorManager.registerListener(pressureListener, preSensor, frequency);
		initEditors();
		Log.i("init", "Yes, I have initialize them.");
	}

	private void initEditors() {
		et_pressure_1.setText(pressures.getString("pressure_1st", "null"));
		et_pressure_2.setText(pressures.getString("pressure_2nd", "null"));
		et_5_1.setText(distances.getString("5-1", "null"));
		et_5_2.setText(distances.getString("5-2", "null"));
		et_5_3.setText(distances.getString("5-3", "null"));
		et_5_4.setText(distances.getString("5-4", "null"));
		et_10_6.setText(distances.getString("10-6", "null"));
		et_10_7.setText(distances.getString("10-7", "null"));
		et_10_8.setText(distances.getString("10-8", "null"));
		et_10_9.setText(distances.getString("10-9", "null"));
	}
	
	

	private void setListener() {
		
		cb_datalock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					et_pressure_1.setEnabled(false);
					et_pressure_2.setEnabled(false);
					et_5_1.setEnabled(false);
					et_5_2.setEnabled(false);
					et_5_3.setEnabled(false);
					et_5_4.setEnabled(false);
					et_10_6.setEnabled(false);
					et_10_7.setEnabled(false);
					et_10_8.setEnabled(false);
					et_10_9.setEnabled(false);
				} else {
					et_pressure_1.setEnabled(true);
					et_pressure_2.setEnabled(true);
					et_5_1.setEnabled(true);
					et_5_2.setEnabled(true);
					et_5_3.setEnabled(true);
					et_5_4.setEnabled(true);
					et_10_6.setEnabled(true);
					et_10_7.setEnabled(true);
					et_10_8.setEnabled(true);
					et_10_9.setEnabled(true);
				}
			}
		});
		
		btn_test.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// change the status of btns
				btn_test.setEnabled(false);
				if (flagPool.test == false) {
					new Thread(new ConnectImpl(MainActivity.this, 2010, 10, tvs_connect[9], tv_detail, dataPool, flagPool, timer)).start();
					flagPool.test = true;
				}
				btn_reset.setEnabled(true);
				//开始录音
				timer.schedule(new TestRecordStart(dataPool, flagPool, tv_detail, tv_result, btn_test, timer, audioRecord), 0);
				long time1 = System.currentTimeMillis();
				while (true) {
					long time2 = System.currentTimeMillis();
					if ((time2-time1) > (long)3) {
						timer.schedule(new Clock(dataPool, ConfigPool.getCurrentIndex()), 300);
						break;
					}
				}
			}
		});
		
		btn_connect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// change the status of btns
				btn_connect.setEnabled(false);
				btn_import.setEnabled(true);
				//第一组
				new Thread(new ConnectImpl(MainActivity.this, 2001, 1, tvs_connect[0], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2002, 2, tvs_connect[1], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2003, 3, tvs_connect[2], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2004, 4, tvs_connect[3], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2005, 5, tvs_connect[4], tv_detail, dataPool, flagPool, timer)).start();
				//第二组
				new Thread(new ConnectImpl(MainActivity.this, 2006, 6, tvs_connect[5], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2007, 7, tvs_connect[6], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2008, 8, tvs_connect[7], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2009, 9, tvs_connect[8], tv_detail, dataPool, flagPool, timer)).start();
				new Thread(new ConnectImpl(MainActivity.this, 2010, 10, tvs_connect[9], tv_detail, dataPool, flagPool, timer)).start();
				btn_reset.setEnabled(true);
			}
		});
		
		btn_import.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Editor editorPressure = pressures.edit();
				editorPressure.putString("pressure_1st", et_pressure_1.getText().toString());
				editorPressure.putString("pressure_2nd", et_pressure_2.getText().toString());
				editorPressure.commit();
				DataPool.setPressures(Float.parseFloat(et_pressure_1.getText().toString()),
						Float.parseFloat(et_pressure_2.getText().toString()));
				Editor editorDistance = distances.edit();
				editorDistance.putString("5-1", et_5_1.getText().toString());
				editorDistance.putString("5-2", et_5_2.getText().toString());
				editorDistance.putString("5-3", et_5_3.getText().toString());
				editorDistance.putString("5-4", et_5_4.getText().toString());
				editorDistance.putString("10-6", et_10_6.getText().toString());
				editorDistance.putString("10-7", et_10_7.getText().toString());
				editorDistance.putString("10-8", et_10_8.getText().toString());
				editorDistance.putString("10-9", et_10_9.getText().toString());
				Log.i("init", et_5_1.getText().toString()+"改动了距离值"+pressures.getString("pressure_1st", "???"));
				DataPool.setDistance_One(Double.parseDouble(et_5_1.getText().toString()),
						Double.parseDouble(et_5_2.getText().toString()),
						Double.parseDouble(et_5_3.getText().toString()),
						Double.parseDouble(et_5_4.getText().toString()));   //第一组
				DataPool.setDistance_Two(Double.parseDouble(et_10_6.getText().toString()),
						Double.parseDouble(et_10_7.getText().toString()),
						Double.parseDouble(et_10_8.getText().toString()),
						Double.parseDouble(et_10_9.getText().toString()));   //第一组
				editorDistance.commit();
				new Thread(new WaitForConnectImpl(flagPool, btn_import, btn_sound)).start();
			}
		});
		
		btn_sound.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				btn_sound.setEnabled(false);
				WriteToText();
				new Thread(new Runnable() {
					public void run() {
						while(true){
							if (! flagPool.readyForNext) {
								continue;
							}
							flagPool.readyForNext = false;
							ConfigPool.setCurrentIndex();
							//开始录音
							timer.schedule(new RecordStart(dataPool, flagPool, btn_sound, tv_result, timer, audioRecord), 0);
							long time1 = System.currentTimeMillis();
							while (true) {
								if (flagPool.feedBackStart == 5) {
									timer.schedule(new Clock(dataPool, ConfigPool.getCurrentIndex()), 300);
									flagPool.feedBackStart = 0;
									break;
								}
								long time2 = System.currentTimeMillis();
								if ((time2-time1) > (long)3) {
									timer.schedule(new Clock(dataPool, ConfigPool.getCurrentIndex()), 300);
									flagPool.feedBackStart = 0;
									break;
								}
							}
						}
					}
				}).start();

//				//开始录音
//				timer.schedule(new RecordStart(dataPool, flagPool, btn_sound, tv_result, timer, audioRecord), 0);
//				long time1 = System.currentTimeMillis();
//				while (true) {
//					if (flagPool.feedBackStart == 5) {
//						timer.schedule(new Clock(dataPool, ConfigPool.getIndex()), 300);
//						flagPool.feedBackStart = 0;
//						break;
//					}
//					long time2 = System.currentTimeMillis();
//					if ((time2-time1) > (long)3) {
//						timer.schedule(new Clock(dataPool, ConfigPool.getIndex()), 300);
//						flagPool.feedBackStart = 0;
//						break;
//					}
//				}
			}
		});
		Log.i("init", "Yes, the listener is already.");
		
		btn_reset.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				timer.schedule(new ResetBeacons(dataPool), 0);
				Intent intent = getIntent();
				finish();
				startActivity(intent);
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
		soundPool.play(spMap.get(sound), 1, 1, 1, number, 1);
		Log.i("init", "在playSounds()方法中");
	}
	
	public void stopSound(){
		if (audioRecord != null) {
			Log.i("IO", "stop Recording...");
			flagPool.setRecordStatus(false);
			audioRecord.stop();
		}
	}
	
	private String getIpAddress()
	{
	    //获取wifi服务  
	    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
	    //判断wifi是否开启  
	    if (!wifiManager.isWifiEnabled()) {  
	    	wifiManager.setWifiEnabled(true);    
	    }  
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    int ipNum = wifiInfo.getIpAddress();
	    return int2Ip(ipNum);
	}
	
	private String int2Ip(int i) {       
	    
	    return (i & 0xFF ) + "." +       
	  ((i >> 8 ) & 0xFF) + "." +       
	  ((i >> 16 ) & 0xFF) + "." +       
	  ( i >> 24 & 0xFF) ;  
	} 
	
	public void WriteToText() {
		try {
			pw = new OutputStreamWriter(new FileOutputStream("/mnt/sdcard/AALocResult.txt", true), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			pw.write(System.currentTimeMillis()+"\r\n");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

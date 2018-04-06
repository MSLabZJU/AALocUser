package com.tristan.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

import com.tristan.aalocuser.ConfigPool;
import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.aalocuser.MainActivity;
import com.tristan.timertask.TargetGo;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ConnectImpl implements Runnable {
	private int port;
	private int index;
	private int visualIndex;
	private TextView status_connect;
	private TextView info;
	private ServerSocket myListener;
	private Socket linkSocket;
	private PrintWriter pw;
	private DataPool dataPool;
	private FlagPool flagPool;
	private Timer timer;
	private MainActivity activity;
	private BufferedReader br;
	
	public ConnectImpl() {
		super();
	}

	public ConnectImpl(MainActivity activity, int port, int index, View status_connect,  View info, 
			DataPool dataPool, FlagPool flagPool, Timer timer){
		this.activity = activity;
		this.port = port;
		this.index = index;
		this.status_connect = (TextView) status_connect;
		this.info = (TextView)info;
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.timer = timer;
		this.visualIndex = (index - 1) % 5 + 1;
	}
	
	public void run() {
		try {
			myListener = new ServerSocket(port);
			//wait for connecting
			linkSocket = myListener.accept();

			
			pw = new PrintWriter(linkSocket.getOutputStream(),true);
			dataPool.setPrintWriter(index, pw);
			Log.i("init", index+": the pw has been set.");
			
			flagPool.setConnect(index, true);
			status_connect.post(new Runnable() {
				public void run() {
					status_connect.setBackgroundColor(Color.GREEN);
				}
			});
			
			while (true) {
				br = new BufferedReader(new InputStreamReader(linkSocket.getInputStream()));
				String keyString = br.readLine();
				while (keyString != null) {
					//如果中心结点对应的线程收到了信号"000"，那么开始发声
					if (keyString.equals("000") && (index == ConfigPool.getIndex())){
						Log.i("init", "接收到结点发过来的000指令，准备发声");
						timer.schedule(new TargetGo(activity, dataPool, flagPool, timer), 400);
					}
					if (keyString.equals("520")) {
						flagPool.feedBackStart ++;
					}
					if (keyString.equals("521")) {
						String t1 = br.readLine();
						String t2 = br.readLine();
						dataPool.setRealPl(visualIndex, Double.parseDouble(t1), Double.parseDouble(t2));
						flagPool.setCompute(visualIndex, true);
						info.post(new Runnable() {
							public void run() {
								info.append("get TD from beacon: "+index+"\n");
							}
						});
					}
					keyString = br.readLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshLogView(TextView textView,  String msg){
        textView.append(msg);
        int offset=textView.getLineCount()*textView.getLineHeight();
        if(offset>textView.getHeight()){
            textView.scrollTo(0,offset-textView.getHeight());
        }
    }

}

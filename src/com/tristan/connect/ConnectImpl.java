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
import android.view.View;
import android.widget.TextView;

public class ConnectImpl implements Runnable {
	private int port;
	private int index;
	private TextView status;
	private TextView info;
	private ServerSocket myListener;
	private Socket linkSocket;
	private PrintWriter pw;
	private DataPool dataPool;
	private FlagPool flagPool;
	private Timer timer;
	private Activity activity;
	private BufferedReader br;
	
	public ConnectImpl() {
		super();
	}

	public ConnectImpl(Activity activity, int port, int index, View info, 
			DataPool dataPool, FlagPool flagPool, Timer timer){
		this.activity = activity;
		this.port = port;
		this.index = index;
		this.info = (TextView)info;
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.timer = timer;
	}
	
	public void run() {
		try {
			myListener = new ServerSocket(port);
			//wait for connecting
			linkSocket = myListener.accept();
			flagPool.setConnect(index, true);
			info.post(new Runnable() {
				public void run() {
					info.append("***connected port: "+port);
				}
			});
			
			pw = new PrintWriter(linkSocket.getOutputStream(),true);
			dataPool.setPrintWriter(index, pw);
			
			while (true) {
				br = new BufferedReader(new InputStreamReader(linkSocket.getInputStream()));
				String keyString = br.readLine();
				while (keyString != null) {
					//如果中心结点对应的线程收到了信号"000"，那么开始发声
					if (keyString.equals("000") && (index == ConfigPool.INDEX)){
						timer.schedule(new TargetGo(activity, dataPool, flagPool, timer), 400);
					}
					if (keyString.equals("520")) {
						flagPool.feedBackStart ++;
					}
					if (keyString.equals("521")) {
						String t1 = br.readLine();
						String t2 = br.readLine();
						dataPool.setRealPl(index, Double.parseDouble(t1), Double.parseDouble(t2));
						flagPool.setCompute(index, true);
						info.post(new Runnable() {
							public void run() {
								info.append("get TD from beacon: "+index);
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

}

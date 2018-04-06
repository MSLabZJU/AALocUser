package com.tristan.timertask;

import java.io.PrintWriter;
import java.util.TimerTask;

import com.tristan.aalocuser.DataPool;

public class ResetBeacons extends TimerTask {
	DataPool dataPool;
	
	public ResetBeacons(DataPool dataPool){
		super();
		this.dataPool = dataPool;
	}
	
	@Override
	public void run() {
		PrintWriter[] pws = dataPool.getPrintWriters();
		for (PrintWriter pw : pws) {
			if (pw != null) {
				pw.println("reset");
			}
		}
	}

}

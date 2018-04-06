package com.tristan.connect;

import android.view.View;
import android.widget.Button;

import com.tristan.aalocuser.FlagPool;

public class WaitForConnectImpl implements Runnable {

	private FlagPool flagPool;
	private Button btn_import;
	private Button btn_sound;
	
	public WaitForConnectImpl(FlagPool flagPool, Button btn_import, Button btn_sound){
		this.flagPool = flagPool;
		this.btn_import = btn_import;
		this.btn_sound = btn_sound;
	}
	
	public void run() {
		while (true) {
			if (flagPool.allConnectOrNot_one() && flagPool.allConnectOrNot_two()) {
				btn_sound.post(new Runnable() {
					
					public void run() {
						btn_sound.setEnabled(true);
					}
				});
				btn_import.post(new Runnable() {
					
					public void run() {
						btn_import.setEnabled(false);
					}
				});
				break;
			}
		}
	}

}

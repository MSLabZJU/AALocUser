/**
 * 
 */
package com.tristan.sensor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.tristan.aalocuser.ConfigPool;
import com.tristan.aalocuser.DataPool;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

/**
 * sensors listener
 * @author TristanHuang
 * 2017-6-28  p.m. 7:55:53
 */
public class PressureListener implements SensorEventListener{
	
	private float pressure;
	private TextView tv_info;
	private TextView tv_layer;
	
	public PressureListener(TextView tv_info, TextView tv_layer){
		super();
		pressure = 0;
		this.tv_info = tv_info;
		this.tv_layer = tv_layer;
	}
	
	public PressureListener(TextView tv_info) {
		super();
		pressure = 0;
		this.tv_info = tv_info;
		this.tv_layer = tv_layer;
	}

	public void onSensorChanged(SensorEvent event) {
		//values[0]: Atmospheric pressure in hPa (millibar) 
		pressure = event.values[0];
		tv_info.post(new Runnable() {
			public void run() {
				tv_info.setText(String.valueOf(pressure));
			}
		});
		if (pressure < DataPool.pressure_mid && DataPool.getFloorID() == 1) {
			DataPool.setFloorID(2);
			ConfigPool.setInstantTimeIndex(10);
			tv_layer.post(new Runnable() {
				public void run() {
					tv_layer.setText("2nd floor");
				}
			});
		} else if (pressure > DataPool.pressure_mid && DataPool.getFloorID() == 2) {
			DataPool.setFloorID(1);
			ConfigPool.setInstantTimeIndex(5);
			tv_layer.post(new Runnable() {
				public void run() {
					tv_layer.setText("1st floor");
				}
			});
		}
	}
 
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onFlushCompleted(Sensor sensor) {}

}

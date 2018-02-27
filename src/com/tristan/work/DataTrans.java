package com.tristan.work;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.tristan.aalocuser.DataPool;

public class DataTrans {
	// 到节点12345的距离
	private double dis1;
	private double dis2;
	private double dis3;
	private double dis4;
	private double dis5;

	private int time;

	OutputStreamWriter pw;

	public void SetData(double d1, double d2, double d3, double d4, double d5, int t) {
		this.dis1 = d1;
		this.dis2 = d2;
		this.dis3 = d3;
		this.dis4 = d4;
		this.dis5 = d5;
		this.time = t;
	}
	
	public void SetData(DataPool dataPool, int t){
		double[] dists = dataPool.getDistTemp();
		this.dis1 = dists[0];
		this.dis2 = dists[1];
		this.dis3 = dists[2];
		this.dis4 = dists[3];
		this.dis5 = dists[4];
		this.time = t;
	}

	public void Print() {
		System.out.println("以下是第" + time + "次实验");
		System.out.println("Distance5 is:" + dis5);
		System.out.println("Distance4 is:" + dis4);
		System.out.println("Distance3 is:" + dis3);
		System.out.println("Distance2 is:" + dis2);
		System.out.println("Distance1 is:" + dis1);
	}

	public void WriteToText() {

		try {
			pw = new OutputStreamWriter(new FileOutputStream("/mnt/sdcard/test.txt", true), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			pw.write("以下是第" + time + "次实验" + "\r\n");
			pw.write("Distance1 is:" + String.valueOf(dis1) + "\r\n");
			pw.write("Distance2 is:" + String.valueOf(dis2) + "\r\n");
			pw.write("Distance3 is:" + String.valueOf(dis3) + "\r\n");
			pw.write("Distance4 is:" + String.valueOf(dis4) + "\r\n");
			pw.write("Distance5 is:" + String.valueOf(dis5) + "\r\n");
			pw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

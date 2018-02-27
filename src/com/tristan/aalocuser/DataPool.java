package com.tristan.aalocuser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;

import com.tristan.algorithm.*;

import android.R.integer;

public class DataPool {
	
	public static final int NODENUM = 5;
	
	private static double[] resultLoc = new double[3]; //定位结果
	private static int loopNum = 5;
	private static double[][] resultLoop = new double[loopNum][3] ; //存循环几次的结果
	private static int loopNumTemp = 0;   //目前正处理的位置
	
	//四个距离值
	public static double fl1 = 10;
	public static double fl2 = 10;
	public static double fl3 = 10;
	public static double fl4 = 10;	
	
	public static int time = 0;
	
	//估计的真实位置
	public static double realpl11,realpl12;
    public static double realpl21,realpl22;
    public static double realpl31,realpl32;
    public static double realpl41,realpl42;
    public static double realpl51,realpl52;
    public static double realpl61,realpl62;
    
    //计算相关的变量
    private static double dis1,dis2,dis3,dis4,dis5;
    private static double num1,num2,num3,num4,num5,num6;
    private static double num56,num15,num25,num35,num45;
    
    //给Beacon发指令的PrintWriter
    private static PrintWriter[] pws;
    
    //用于存放录音文件的与 文件夹名称的相关标志位
	public static int FolderNum = 1;
	//录音文件文件名称的相关标志位
	public static int FileNum = 0;
	
	private static String rawfileName;
	private static String wavfileName;
	public static final String REFERAUDIO_STRING = "/mnt/sdcard/data_refer.wav";
    
	//************构造器区域*****************
	private static DataPool instance = new DataPool();
	
	private DataPool(){
		super();
	}
	
	public static DataPool getInstance(){
		return instance;
	}
	//****************************************
	
	public void sendEndSignal(){
		for (PrintWriter pw : pws) {
			pw.println("01");   //01作为结束录音的标志位
		}
	}
	
	public double[] progressOne(){
		time++;
		num1 = Math.abs(realpl11-realpl12);
		num2 = Math.abs(realpl21-realpl22);
		num3 = Math.abs(realpl31-realpl32);
		num4 = Math.abs(realpl41-realpl42);
		num5 = Math.abs(realpl51-realpl52);
		num6 = Math.abs(realpl61-realpl62);
		
		/*
		 *            num5
		 * -------------------------------  node
		 *    \  |            |  /
		 *     \ |            | /
		 *      \|   target   |/
		 * -------------------------------  target
		 */
		num56 = num5 - num6;
		dis5 = (num56/(double)2/44100)*340;//计算目标节点和5号节点的距离
		
		num15 = num1 - num5;
		dis1 = dis5 + (num15/(double)44100+(fl1/340))*340;
		
		num25 = num2 - num5;
		dis2 = dis5 + (num25/(double)44100+(fl2/340))*340;
		
		num35 = num3 - num5;
		dis3 = dis5 + (num35/(double)44100+(fl3/340))*340;
		
		num45 = num4 - num5;
		dis4 = dis5 + (num45/(double)44100+(fl4/340))*340;
		
		List<double[]> xnode = new ArrayList<double[]>();
		xnode.add(new double[]{8.615, 0.079, 4.110});
		xnode.add(new double[]{22.888, 0, 6.167});
		xnode.add(new double[]{22.888, 10.787, 6.007});
		xnode.add(new double[]{8.65, 6.245, 4.087});
		xnode.add(new double[]{14.895, 4.968, 9.265});
		
		double[] area = {0.0, 40.0, 0.0, 15.0, 0.0, 10.0};
		double[] dist = {dis1, dis2, dis3, dis4, dis5};
		Dist3DMLGrid util = Dist3DMLGrid.getInstance();
		double[] resultTemp = util.compute(xnode, dist, area);
		
		for (int i = 0; i < 3; i++) {
			resultLoop[loopNumTemp][i] = resultTemp[i+1];
		}
		loopNumTemp++;

		return getDistTemp();
	}
	
	public double[] getDistTemp(){
		return new double[]{dis1, dis2, dis3, dis4, dis5};
	}
	
	public boolean isLoopEnd(){
		return loopNum == loopNumTemp;
	}
	
	/**
	 * 使用该方法前需要检查 isLoopEnd()，返回true才可以调用
	 * @return
	 */
	public double[] progressTwo(){
		loopNumTemp = 0;
		double[] coorx = new double[loopNum];
		double[] coory = new double[loopNum];
		double[] coorz = new double[loopNum];
		
		for (int j = 0; j < loopNum; j++) {
			coorx[j] = resultLoop[j][0];
			coory[j] = resultLoop[j][1];
			coorz[j] = resultLoop[j][2];
		}
		GenerateNumber gnx = new GenerateNumber(coorx);
		GenerateNumber gny = new GenerateNumber(coory);
		GenerateNumber gnz = new GenerateNumber(coorz);

		resultLoc[0] = gnx.GetResult();
		resultLoc[1] = gny.GetResult();
		resultLoc[2] = gnz.GetResult();
		
		double[] resultForReturn = Arrays.copyOf(resultLoc, 3); 
		return resultForReturn;
	}
	
	public String getRawfileName() {
		return "/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".raw";
	}
	
	public String getWavfileName() {
		return "/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".wav";
	}
	
	public String getFolderName(){
		return "mnt/sdcard/Data" + String.valueOf(FolderNum);
	}
	
    public void setPrintWriter(int index, PrintWriter pw){
    	pws[index-1] = pw;
    }
    
    public PrintWriter[] getPrintWriters(){
    	return pws;
    }
    
    public static void setDistance(double valOne, double valTwo, double valThree, double valFour){ 
    	fl1 = valOne;
    	fl2 = valTwo;
    	fl3 = valThree;
    	fl4 = valFour;
    }
    
    public void setRealPl(int index, double valOne, double valTwo) {
		switch (index) {
		case 1:
			realpl11 = valOne;
			realpl12 = valTwo;
			break;
		case 2:
			realpl21 = valOne;
			realpl22 = valTwo;
			break;
		case 3:
			realpl31 = valOne;
			realpl32 = valTwo;
			break;
		case 4:
			realpl41 = valOne;
			realpl42 = valTwo;
			break;
		case 5:
			realpl51 = valOne;
			realpl52 = valTwo;
			break;
		case 6:
			realpl61 = valOne;
			realpl62 = valTwo;
			break;
		default:
			break;
		}
	}
}

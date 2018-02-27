package com.tristan.algorithm;

import java.util.Arrays;
import java.util.List;

public class Dist3DMLGrid {

	private static final int ROW = 0;		// 行标志
	private static final int COLUMN = 1;	// 列标志

	private static final int XMIN = 0;
	private static final int XMAX = 1;
	private static final int YMIN = 2;
	private static final int YMAX = 3;
	private static final int ZMIN = 4;
	private static final int ZMAX = 5;
	
	private static final int PX = 0;
	private static final int PY = 1;
	private static final int PZ = 2;
	
	private static final double EPSILON = 0.01;	
	private static final int mN = 50;		// 最大迭代次数
	private static final int stepDi = 10;	//
	
	private static final int DIMENSION = 3;
	
	private static Dist3DMLGrid instance = new Dist3DMLGrid();
	
	// Singleton
	private Dist3DMLGrid() { }
	// 获取类实例
	public static Dist3DMLGrid getInstance() {
		return instance;
	}

	/**
	 * 三维最大似然定位估计，搜索方式用网格搜索，方差采用所有量测一致性
	 * @param xnode 信标节点位置
	 * @param dist 节点到目标位置
	 * @param area 目标可行区域
	 */
	public double[] compute(List<double[]> xnode, final double[] dist,final double[] area) {
		// 初始化
		double[] tPos = null;
		// 异常检测
		if (xnode == null || dist == null || area == null) {
			return null;
		}		
		if (xnode.size() == 0 || dist.length == 0 || area.length == 0) {
			return null;
		}
		int[] dim1 = new int[]{dist.length, 1};
		int[] dim2 = new int[]{xnode.size(), xnode.get(0).length};
		// 输入维度不对，或节点过少时，或初值设置为零，返回空集
		if (dim1[ROW] < 4 || dim1[ROW] != dim2[ROW] || dim2[COLUMN] != DIMENSION) {
			return null;
		}
		int ns = dim1[ROW];
		// 最大似然 Newton_Raphson
		int mCount = 0;
		
//		List<double[]> deltaD = new ArrayList<>();
//		List<double[]> tmpPos = new ArrayList<>();
		while (true) {
			mCount++;
			double[] delta = new double[3];
			delta[PX] = (area[XMAX] - area[XMIN]) / stepDi;
			delta[PY] = (area[YMAX] - area[YMIN]) / stepDi;
			delta[PZ] = (area[ZMAX] - area[ZMIN]) / stepDi;
			double[][] xyz = new double[DIMENSION][stepDi];		//MATLAB中的Sxyz

			for (int i = 0; i < stepDi; i++) {
				xyz[PX][i] = (area[XMIN] + delta[PX] / 2) + i * delta[PX];
				xyz[PY][i] = (area[YMIN] + delta[PY] / 2) + i * delta[PY];
				xyz[PZ][i] = (area[ZMIN] + delta[PZ] / 2) + i * delta[PZ];
			}
			// 计算当前位置到传感器距离d
			double[][] lnPVec = new double[stepDi*stepDi*stepDi][DIMENSION+1];
			double[] lnP = new double[stepDi*stepDi*stepDi];
			for (int i = 0; i < stepDi; i++) {
				for (int j = 0; j < stepDi; j++) {
					for (int k = 0; k < stepDi; k++) {
						double[] tmpLoc = new double[] {xyz[PX][i], xyz[PY][j], xyz[PZ][k]};
						double[] d = new double[ns];
						for (int kk = 0; kk < ns; kk++) {
							d[kk] = norm(tmpLoc, xnode.get(kk));
							d[kk] -= dist[kk];			// 对应res = sum((D-dist).^2);中的减法
							d[kk] *= d[kk];				// 对应res = sum((D-dist).^2);中的乘方
						}
						double resTemp = sum(d);
						double[] tmp = new double[]{resTemp,tmpLoc[PX],tmpLoc[PY],tmpLoc[PZ]};
						lnP[i * 100 + j * 10 + k] = resTemp;
						lnPVec[i * 100 + j * 10 + k] = tmp;
					}
				}
			}
			
			int index = minIndex(lnP);
			area[XMIN] = lnPVec[index][1] - delta[PX];
			area[YMIN] = lnPVec[index][2] - delta[PY];
			area[ZMIN] = lnPVec[index][3] - delta[PZ];
			area[XMAX] = lnPVec[index][1] + delta[PX];
			area[YMAX] = lnPVec[index][2] + delta[PY];
			area[ZMAX] = lnPVec[index][3] + delta[PZ];	

//			deltaD.add(delta);
			if (norm(delta) < EPSILON || mCount > mN) {
				tPos = lnPVec[index];				
				System.out.println("mCount=" + mCount);
				break;
			}
		}
		return tPos;
	}
	// 计算距离
	private double norm(double[] a, double[] b) {
		if (a == null || b == null || a.length != b.length) {
			throw new IllegalArgumentException("invalid input parameter for norm()");
		}
		int length = a.length;
		double sum = 0.0;
		for (int i = 0; i < length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}
		return Math.sqrt(sum);
	}
	
	private double norm(double[] a) {
		if (a == null) {
			throw new IllegalArgumentException("invalid input parameter for norm()");
		}
		int length = a.length;
		double[] b = new double[length];
		Arrays.fill(b, 0.0);
		return norm(a, b);
	}
	
	private double sum(double[] a) {
		if (a == null) {
			throw new IllegalArgumentException();
		}
		int length = a.length;
		double sum = 0.0;
		for (int i = 0; i < length; i++) {
			sum += a[i];
		}
		return sum;
	}
	
	private int minIndex(double[] a) {
		if (a == null || a.length == 0) {
			return -1;
		}
		int length = a.length;
		int index = 0;
		double min = a[0];
		for (int i = 0; i < length; i++) {
			if (min > a[i]) {
				min = a[i];
				index = i;
			}
		}
		return index;
	}
	
	private int maxIndex(double[] a) {
		if (a == null || a.length == 0) {
			return -1;
		}
		int length = a.length;
		int index = 0;
		double max = a[0];
		for (int i = 0; i < length; i++) {
			if (max < a[i]) {
				max = a[i];
				index = i;
			}
		}
		return index;
	}
	
}
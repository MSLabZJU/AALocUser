package com.tristan.work;

import java.io.File;

import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;
import com.tristan.signal.FFTcc;
import com.tristan.signal.FFTprepare;
import com.tristan.signal.FindPeaksNew;

import android.R.integer;
import android.R.string;

/**
 * 
 * @author TristanHuang
 * 2018-2-26  ÉÏÎç10:56:20
 */
public class ComputeImpl implements Runnable {

	private String wavfilePathString;
	private DataPool dataPool;
	private FlagPool flagPool;
	
	public ComputeImpl(String wavfilePathString, DataPool dataPool, FlagPool flagPool) {
		this.wavfilePathString = wavfilePathString;
		this.dataPool = dataPool;
		this.flagPool = flagPool;
	}
	
	public void run() {
		while (true) {
			if (new File(wavfilePathString).exists()) {
				WaveFileReader wfr_sig = new WaveFileReader(wavfilePathString);
				WaveFileReader wfr_refer = new WaveFileReader(DataPool.REFERAUDIO_STRING);
				float[] signal = wfr_sig.getData()[0];
				float[] refer = wfr_refer.getData()[0];
				int signalLength = wfr_sig.getDataLen();
				int referLength = wfr_refer.getDataLen();
				
				FFTprepare fpp = new FFTprepare(signal);
				float[] signalFFT = fpp.getsig();
				
				FFTcc fcc = new FFTcc(signalFFT, refer);
				fcc.FindFccMax();
				float[] rcc = fcc.getrcc();
				float max = fcc.getmaxvalue();
				int maxplace = fcc.getmaxplace();
				
				int rccLength = rcc.length;
				float[] rccn;
				
				if (signalLength < 2400)
					signalLength = 4096;
				
				if (signalLength <= rccLength) {
					rccn = new float[signalLength];
					for (int i = signalLength - 1; i >= 0; i--) {
						rccn[i] = rcc[rccLength - 1];
						rccLength--;
					}
				} else {
					rccn = rcc;
				}
				FindPeaksNew fpn = new FindPeaksNew(rccn);
				int[] real = new int[2];
				real = fpn.getresult();
				dataPool.setRealPl(6, real[0], real[1]);
				flagPool.setCompute(6, true);
				break;
			}
		}
		
	}

}

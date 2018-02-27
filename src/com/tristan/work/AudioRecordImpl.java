package com.tristan.work;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

import android.media.AudioRecord;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tristan.aalocuser.ConfigPool;
import com.tristan.aalocuser.DataPool;
import com.tristan.aalocuser.FlagPool;

/**
 * 
 * @author TristanHuang 2018-2-25 下午10:55:24
 */
public class AudioRecordImpl implements Runnable {

	private DataPool dataPool;
	private FlagPool flagPool;
	private AudioRecord audioRecord;
	private String rawfile;
	private String wavfile;
	private Button btn;
	private Timer timer;

	public AudioRecordImpl(DataPool dataPool, FlagPool flagPool, Button btn, Timer timer, AudioRecord audioRecord) {
		this.dataPool = dataPool;
		this.flagPool = flagPool;
		this.btn = btn;
		this.timer = timer;
		this.audioRecord = audioRecord;
	}

	public void run() {
		dataPool.FileNum++;
		rawfile = dataPool.getRawfileName();
		wavfile = dataPool.getWavfileName();
		writeData2File(rawfile); // 往文件中写入裸数据
		copyWaveFile(rawfile, wavfile); // 得到可以播放的wav文件
		new File(rawfile).delete();
		new Thread(new ComputeImpl(wavfile, dataPool, flagPool)).start();
		new Thread(new DistanceCompute(dataPool, flagPool, btn, timer, audioRecord)).start();
	}

	/**
	 * 将裸音文件转换成可以播放的音频文件
	 * 
	 * @param rawfile
	 * @param wavfile
	 */
	private void copyWaveFile(String rawfile, String wavfile) {
		FileInputStream in = null;
		FileOutputStream out = null;

		// params for function writeWaveFileHeader()
		long totalAudioLength = 0;
		long totalDataLength = totalAudioLength + 36;
		long longSampleRate = ConfigPool.FREQUENCY;
		int channels = 2;
		long byteRate = 16 * ConfigPool.FREQUENCY * channels / 8;

		// 缓冲数组
		byte[] data = new byte[ConfigPool.BUFFERSIZE];

		try {
			in = new FileInputStream(rawfile);
			out = new FileOutputStream(wavfile);

			totalAudioLength = in.getChannel().size();
			totalDataLength = totalAudioLength + 36;

			writeWaveFileHeader(out, totalAudioLength, totalDataLength, longSampleRate, channels, byteRate);

			// 将in中的数据通过缓冲数组data写入到out中
			int size = 0;
			while ((size = in.read(data)) != -1) {
				Log.i("IO", "copyWaveFile..." + size);
				out.write(data, 0, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeData2File(String fileName) {
		// 用来存一些字节数据，大小为缓冲区的大小
		byte[] audioData = new byte[ConfigPool.BUFFERSIZE];
		FileOutputStream fos = null;
		int readSize = 0;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			// 建立一个可存取字节的文件
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (flagPool.isRecord) {
			// 从声卡硬件读取数据，用来填充audioData数组，并返回数组填充数据的大小
			readSize = audioRecord.read(audioData, 0, ConfigPool.BUFFERSIZE);
			if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
				try {
					System.out.println("writeDateTOFile..." + readSize);
					// 将audioData中的数据写进输出文件流fos
					fos.write(audioData, 0, readSize);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下面提供一个头文件信息。插入这些信息就可以得到可以播放的文件。这些信息是WAV音频文件所必须的，
	 * 而且所有该格式的文件都是一样的，当然对于格式的文件也有相应的文件头
	 */
	private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
			int channels, long byteRate) throws IOException {

		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;// format=1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);// block align
		header[33] = 0;
		header[34] = 16;// bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

}

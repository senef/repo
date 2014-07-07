package com.stage.pong.dtmf;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;

@SuppressLint("NewApi")
public class TonesGenerator {
	ToneGenerator toneGen;
	AudioTrack track = null;
	private static final int fe = 48000;
	int TAILLE_BUFFER = 1920;
	short[] buffer = new short[18 * TAILLE_BUFFER];;

	// Test all DTMF tones one by one

	public TonesGenerator() {
		super();
		toneGen = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
		init();
	}

	void init() {
		int maxJitter = AudioTrack.getMinBufferSize(fe,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		track = new AudioTrack(AudioManager.STREAM_DTMF, fe,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				maxJitter, AudioTrack.MODE_STREAM);
		track.play();
		
	}

	public boolean tonesDtmfTest() throws Exception {

		int type;

		for (type = ToneGenerator.TONE_DTMF_0; type <= ToneGenerator.TONE_DTMF_D; type++) {
			toneGen.startTone(type, 400);
			toneGen.stopTone();
			Thread.sleep(200);

		}
		toneGen.release();
		return true;
	}

	public void play(int toneType) {
		toneGen.startTone(toneType, 40);
		toneGen.stopTone();
	}

	public int[] intToBinary(int n) {
		int marge;
		String pack = "";
		String binaryString = Integer.toBinaryString(n);

		String str[] = binaryString.split("");
		marge = 9 - str.length + 1;

		int[] binaries = new int[9];
		for (int k = 0; k < marge; k++) {
			binaries[k] = 0;
			pack = pack + binaries[k];
		}

		for (int i = 1; i < str.length; i++) {
			binaries[i - 1 + marge] = Integer.valueOf(str[i]);
			pack = pack + binaries[i - 1 + marge];
		}
		System.out.println("debugage bin =" + pack);
		return binaries;

	}

	
	public int[] intToBinary(int n1, int n2) {
		int marge1, marge2;
		String pack = "";
		String binaryString1 = Integer.toBinaryString(n1);
		String binaryString2 = Integer.toBinaryString(n2);

		String str1[] = binaryString1.split("");
		String str2[] = binaryString2.split("");
		marge1 = 9 - str1.length + 1;
		marge2 = 9 - str2.length + 1;

		int[] binaries = new int[18];
		for (int k = 0; k < marge1; k++) {
			binaries[k] = 0;
			pack = pack + binaries[k];
		}

		for (int i = 1; i < str1.length; i++) {
			binaries[i - 1 + marge1] = Integer.valueOf(str1[i]);
			pack = pack + binaries[i - 1 + marge1];
		}
		
		for (int k = 9; k < 9+marge2; k++) {
			binaries[k] = 0;
			pack = pack + binaries[k];
		}

		for (int i = 1; i < str2.length; i++) {
			binaries[i - 1 +9+ marge2] = Integer.valueOf(str2[i]);
			pack = pack + binaries[i - 1+9 + marge2];
		}
		System.out.println("debugage bin =" + pack);
		return binaries;

	}

	
	
	
	public void sendSequence(int[] sequence) throws InterruptedException {
		// Thread.sleep(40);
		for (int i = 0; i < sequence.length; i++) {
			play(sequence[i]);
		 Thread.sleep(40);
		}
		//toneGen.release();
	}

	public void sendPosition(int[] n) throws InterruptedException {
		if(n[1]<0)
			n[1]=n[1]*-1;
		 sendSequence( intToBinary(n[0],n[1]));
		
		
		/*buffer = generate(intToBinary(n[0]));
		System.out.println("debugage ok 1");
		track.write(buffer, 0, buffer.length);
	
		if(n[1]<0)
			n[1]=n[1]*-1;
		buffer = generate(intToBinary(n[1]));
		
	track.write(buffer, 0, buffer.length);*/
	}


	short[] generate(int[] n) {
		short[] buffer= new short[0];
		for (int i = 0; i < n.length; i++) {
			if (n[i] == 0) {
				buffer=this.concat(buffer,genererSignalDeuxfrequence(1336, 941));
			} else if (n[i] == 1) {
				buffer=this.concat(buffer,genererSignalDeuxfrequence(1209, 697));
			}
		}
		return buffer;
	}

	public short[] genererSignalDeuxfrequence(double freq1, double freq2) {
		short[] s = new short[2 * TAILLE_BUFFER];

		for (int i = 0; i < s.length; i++) {
			if (i < 1920) {

				s[i] = (short) ((Math.cos(2 * Math.PI * freq1 / fe * i) + Math
						.cos(2 * Math.PI * freq2 / fe * i)) * 1000);
			}
		}
		return s;

	}

	short[] concat(short[] s1, short[] s2) {
		short[] newArray = new short[s1.length + s2.length];
		System.arraycopy(s1, 0, newArray, 0, s1.length);
		System.arraycopy(s2, 0, newArray, s1.length, s2.length);
		return newArray;
	}
	int[] concat(int[] s1, int[] s2) {
		int[] newArray = new int[s1.length + s2.length];
		System.arraycopy(s1, 0, newArray, 0, s1.length);
		System.arraycopy(s2, 0, newArray, s1.length, s2.length);
		return newArray;
	}
}

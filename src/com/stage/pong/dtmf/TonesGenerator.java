package com.stage.pong.dtmf;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.ToneGenerator;

@SuppressLint("NewApi") public class TonesGenerator {
	ToneGenerator toneGen;
	// Test all DTMF tones one by one
	
	public TonesGenerator() {
		super();
		toneGen = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
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
	
	
	public void play(int toneType){
		toneGen.startTone(toneType, 40);
		toneGen.stopTone();
	}
	
	public void send(int n){
		
	}
	public int[] intToBinary(int n){
		
		String binaryString=Integer.toBinaryString(n);
		String str[] =  binaryString.split(",");
		int [] binaries= new int[str.length];
		for(int i=0;i<str.length;i++){
			binaries[i]=Integer.valueOf(str[i]);
		}
		return binaries;
		
	}
	public void sendSequence(int [] sequence) throws InterruptedException{
		
		for (int i=0;i<sequence.length;i++){
			 play(sequence[i]);
			Thread.sleep(40);	
		}
		toneGen.release();
	}
}

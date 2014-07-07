package com.stage.pong.dtmf;

import android.media.ToneGenerator;

public class Encoder {
	TonesGenerator Gen;

	public Encoder() {
		super();
		Gen = new TonesGenerator();
	}

	public void searchForPlayer() {
	}

	public void invitePlayer() {
		Gen.play(ToneGenerator.TONE_DTMF_P);
	}

	public void joinPlayer() {
		Gen.play(ToneGenerator.TONE_DTMF_S);
	}

	public void sendBuffer(String s) {
		System.out.println("debugage s ="+s );
		String str[] = s.split(",");
		int  [] coord = new int [2];
		double a = Double.valueOf(str[0]);
		double b = Double.valueOf(str[1]);
		
		coord[0]=(int)a;
		coord[1]=(int)b;
		
	
		try {
			Gen.sendPosition(coord);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendScore(String s) {

	}
}

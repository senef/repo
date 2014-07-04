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

}

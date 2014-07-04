package com.stage.pong.test;

import android.media.AudioRecord;

public class TestAudio  extends AudioRecord{

	public TestAudio(int audioSource, int sampleRateInHz, int channelConfig,
			int audioFormat, int bufferSizeInBytes)
			throws IllegalArgumentException {
		super(audioSource, sampleRateInHz, channelConfig, audioFormat,
				bufferSizeInBytes);
		
		// TODO Auto-generated constructor stub
	}
	
	
	

}

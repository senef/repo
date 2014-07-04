package com.stage.pong.dtmf;

import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Decoder {

	//
	boolean detect = true;
	int counter = 0;

	// ext
	int frequency = 48000;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	int blockSize = 480;
	AudioRecord audioRecord;
	short[] signal;

	// Constantes
	private static final double SEUIL = 8 * Math.pow(10, 11);
	private int TOUCHTONESSIZE = 6;
	private static final int fe = 48000;
	private static final int N = 1920;// nb echantillon

	// nb de frequences qui seront detectées à la sortie du filtre
	double[] k;
	double[] cos;
	double[] frequencesADetecter;
	String TAG = "DecoderTest";
	int touchTones = 0;
	String paquetRecu = "";
	int compteur = 0;
	double[][] xbuff;

	// ecriture dans un fichier

	public Decoder() {
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void start() {
		Thread recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listenToMIC();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		recordingThread.start();
	}

	public double getAmplitudeCarre(double x, double y, double coeff) {

		return x * x + y * y - x * y * coeff;

	}

	private void init() throws IOException {

		initFrequenciesToDetect();
	}

	public void initFrequenciesToDetect() {

		// 8 frequences au total
		this.frequencesADetecter = new double[8];

		// 4 frequences hautes
		this.frequencesADetecter[0] = (double) 1209;
		this.frequencesADetecter[1] = (double) 1336;
		this.frequencesADetecter[2] = (double) 1477;
		this.frequencesADetecter[3] = (double) 1633;

		// et 4 frequences basses
		this.frequencesADetecter[4] = (double) 697;
		this.frequencesADetecter[5] = (double) 770;
		this.frequencesADetecter[6] = (double) 852;
		this.frequencesADetecter[7] = (double) 941;

		initKTerms();
	}

	public void initKTerms() {
		k = new double[8];
		for (int i = 0; i < this.frequencesADetecter.length; i++) {
			this.k[i] = (double) (frequencesADetecter[i] / fe * N);
		}
		initCos();
	}

	public void initCos() {
		cos = new double[k.length];
		for (int i = 0; i < k.length; i++) {
			cos[i] = Math.cos(2 * Math.PI * k[i] / N);
		}
	}


	public void decodeCharacter(double freq1, double freq2) {

		String s = match(freq1, freq2);
		if (s.equals("#")) {
			// invitation : show dialog d'invite
		} else if (s.equals("*")) {
			// invitation accepté : demarrer le jeu
		} else if ((Integer.valueOf(s) > 1) && (Integer.valueOf(s) < 10)) {
			this.TOUCHTONESSIZE = Integer.valueOf(s);
			this.paquetRecu = "0";
			this.touchTones = 0;
		} else if ((Integer.valueOf(s) == 1) || (Integer.valueOf(s) == 0)) {
			paquetRecu = this.paquetRecu +","+ s;
			this.touchTones++;
			if (this.touchTones == this.TOUCHTONESSIZE) {
				int n=binaryToInt(paquetRecu);
				//appel au handler
				this.paquetRecu = "0";
				this.touchTones = 0;

			}
		}
	}

	public int binaryToInt(String s) {
		int n = 0;
		String str[] =  s.split(",");
		for (int i = 0; i < str.length - 1; i++) {
			n = 2 * n + 2 * Integer.valueOf(str[i]);
		}
		if (Integer.valueOf(str[str.length - 1]) == 1)
			n = n + 1;
		return n;
	}

	public String match(double f1, double f2) {

		switch (String.valueOf((int) f1)) {
		case "1209":
			switch (String.valueOf((int) f2)) {
			case "697":
				return "1";
			case "770":
				return "4";
			case "852":
				return "7";
			case "941":
				return "*";
			}
		case "1336":
			switch (String.valueOf((int) f2)) {
			case "697":
				return "2";
			case "770":
				return "5";
			case "852":
				return "8";
			case "941":
				return "0";
			}
		case "1477":
			switch (String.valueOf((int) f2)) {
			case "697":
				return "3";
			case "770":
				return "6";
			case "852":
				return "9";
			case "941":
				return "#";
			}
		case "1633":
			switch (String.valueOf((int) f2)) {
			case "697":
				return "A";
			case "770":
				return "B";
			case "852":
				return "C";
			case "941":
				return "D";
			}

		}

		return null;

	}

	// Goertzel algorithm is running
	public void runAlgo(short[] data) {
		double y2[] = new double[8];
		double ymax;
		for (int j = 0; j < data.length; j++) {

			ymax = 0;
			if (detect) {
				for (int i = 0; i < k.length; i++) {

					y2[i] = doAlgok(data[j], i);
					if (y2[i] > ymax) {

						ymax = y2[i];
					}
				}

				if ((ymax > SEUIL)) {
					// On a dépassé le seuil en terme d'amplitude : ca résonne!
					detect = false;
					counter = 0;
					// mise à zéro des buffers
					this.xbuff = new double[3][8];
					// décodage des deux fréquence
					decode(y2);
				}
			} else {
				// on attend avant la prochaine detection de signal
				counter++;
				if (counter > N) {
					detect = true;
				}
			}
		}

	}

	// calcul de l'amplitude du signal en fonction du temps pour le kième filtre
	public double doAlgok(short data, int i) {
		double xtemp;
		double y2;
		xtemp = data + 2 * cos[i] * xbuff[2][i] - xbuff[1][i];
		y2 = getAmplitudeCarre(xtemp, xbuff[2][i], 2 * cos[i]);
		xbuff[1][i] = xbuff[2][i];
		xbuff[2][i] = xtemp;
		return y2;

	}

	// decode si possible le couple de frequence
	public void decode(double[] d) {
		// Log.i(TAG, "test :" + getDetectedFreq(d)[0]+ "," +
		// getDetectedFreq(d)[1]);
		double a = getDetectedFrequencies(d)[0];
		double b = getDetectedFrequencies(d)[1];
		double z = 0;
		if (b > a) {
			z = a;
			a = b;
			b = z;
		}
		if (isInFrequencies(a, 0) && isInFrequencies(b, 4)) {
			// Log.i(TAG, "test :" +a+ "," +b);
			this.decodeCharacter(a, b);
		}

	}

	// teste si la frequence est incluse une des 8 frequences de meme ordre de
	// grandeur
	public boolean isInFrequencies(double freq, int marge) {
		boolean b = false;
		if (marge != 0 && marge != 4) {
			System.err.println("int marge doit etre egale à 0 ou 4");
			return false;
		} else {
			for (int i = marge; i < marge + 4; i++) {
				if (this.frequencesADetecter[i] == freq) {
					b = true;
				}
			}
		}
		return b;
	}

	public double[] getDetectedFrequencies(double[] d) {
		double[] frequencies = new double[2];
		int MAX1 = getMax(d, -1);
		int MAX2 = getMax(d, MAX1);
		if (MAX1 == -1 || MAX2 == -1) {
			frequencies[0] = 0;
			frequencies[1] = 0;
		} else {
			frequencies[0] = this.frequencesADetecter[MAX1];
			frequencies[1] = this.frequencesADetecter[MAX2];
		}
		return frequencies;
	}

	public int getMax(double[] d, int n) {
		int max;
		if (n == 0)
			max = 1;
		else
			max = 0;

		for (int i = 1; i < 8; i++) {
			if (d[i] > d[max] && i != n) {
				max = i;
			}
		}

		if (d[max] != 0)
			return max;
		else
			return -1;

	}

	public void process(short[] data) throws IOException {
		for (int j = 0; j < data.length; j++) {

			if (data[j] != 0 && compteur < N) {
				signal[compteur] = data[j];
				compteur = compteur + 1;
			}

		}

		if (compteur == N) {
			runAlgo(signal);

			reset();

		}

	}

	public void reset() {
		// TODO Auto-generated method stub

		signal = new short[N];
		xbuff = new double[3][8];
		compteur = 0;
	}

	public void listenToMIC() throws IOException {

		reset();

		int bufferSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, bufferSize);
		ArrayList<short[]> buffers = new ArrayList<short[]>();
		try {

			short[] buffer = new short[blockSize];
			int bufferReadSize = 0;
			audioRecord.startRecording();

			while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {

				bufferReadSize = audioRecord.read(buffer, 0, blockSize);
				/*
				 * if( bufferReadSize<this.blockSize){ audioRecord.read(buffer,
				 * bufferReadSize-1, blockSize); }
				 */
				process(buffer);

			}

		} catch (Throwable t) {
			Log.e("AudioRecord", "Recording Failed");
		}

		audioRecord.stop();
		audioRecord.release();
		audioRecord = null;

	}

	// mise en veille
	public void goSleep(int k) {
		long t0, t1;
		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		} while ((t1 - t0) < k);
	}

}

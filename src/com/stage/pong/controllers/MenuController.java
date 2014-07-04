package com.stage.pong.controllers;

import java.net.InetAddress;

import com.stage.pong.dtmf.Decoder;
import com.stage.pong.dtmf.Encoder;
import com.stage.pong.modeles.Ball;
import com.stage.pong.udpconnection.PongCommunication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuController extends Activity {
	public static final int MESSAGE_ASK = 2;
	public static final int MESSAGE_RASK = 7;
	private Button button2P;
	private Button button1P;
	private Button join;
	private Context ctx;
	private PongCommunication PC;
	private TextView txt;
	private String remoteIP = null;
	private Encoder encoder;
	private Decoder decoder;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MESSAGE_ASK:
				
				String a = (String) msg.obj;
				String s = "pong/" + a;
				byte[] send = s.getBytes();
				PC.write(send);
				showDialog("falilou");
				// startGame(null,"2P");
				break;
			case MESSAGE_RASK:

				remoteIP = (String) msg.obj;

				txt.setText("joueur connecté : " + remoteIP);

				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.ctx = this;
		this.encoder = new Encoder();
		this.decoder = new Decoder();
		this.setContentView(R.layout.main_menu);

	}

	protected void startGame(String r, String joueur) {
		// TODO Auto-generated method stub

		Bundle bundle = new Bundle();
		bundle.putString("ip", r);
		bundle.putString("jp", joueur);
		Intent i = new Intent(this, PongControllerTwoPlayer.class);
		i.putExtras(bundle);
		PC.stop();
		startActivity(i);
	}

	@Override
	public void onStart() {
		super.onStart();
		PC = new PongCommunication(this, this.mHandler);
		PC.start();

		this.button2P = (Button) this.findViewById(R.id.button2P);
		this.button1P = (Button) this.findViewById(R.id.button1P);
		this.join = (Button) this.findViewById(R.id.buttonJoin);
		txt = (TextView) findViewById(R.id.textViewIPP);

		this.button2P.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// envoyer un ping et rester en attente d'un pong
				Pinger();

			}
		});

		this.button1P.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startGame(remoteIP, "1P");

			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// this.PC.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PC.stop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		PC.stop();
		PC.start();
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void Pinger() {

		byte[] send = "ping/ping".getBytes();
		PC.write(send);

	}

	public void findSomeone() {
		encoder.searchForPlayer();
	}

	public void inviteSomeone() {
		encoder.invitePlayer();
	}

	public void showDialog(String s) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("INVITE");

		// set dialog message
		alertDialogBuilder
				.setMessage("Do you want to play with " + s)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startGame(null, "2P");
							}
						})
				.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				})

		;
		alertDialogBuilder.show();

	}

}

package com.stage.pong.modeles;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.text.format.Formatter;
import android.util.Log;

public class UserDatas {
	private String PSEUDO;
	private String NB_WINS;
	private int N_PORT;
	private String IP_ADRESS;
	
	
	

	public UserDatas(String pSEUDO, String nB_WINS, int n_PORT, String iP_ADRESS) {
		super();
		PSEUDO = pSEUDO;
		NB_WINS = nB_WINS;
		N_PORT = n_PORT;
		IP_ADRESS = iP_ADRESS;
	}

	
	



	public String getPSEUDO() {
		return PSEUDO;
	}






	public void setPSEUDO(String pSEUDO) {
		PSEUDO = pSEUDO;
	}






	public String getNB_WINS() {
		return NB_WINS;
	}






	public void setNB_WINS(String nB_WINS) {
		NB_WINS = nB_WINS;
	}






	public int getN_PORT() {
		return N_PORT;
	}






	public void setN_PORT(int n_PORT) {
		N_PORT = n_PORT;
	}






	public String getIP_ADRESS() {
		return IP_ADRESS;
	}






	public void setIP_ADRESS(String iP_ADRESS) {
		IP_ADRESS = iP_ADRESS;
	}






	public String getLocalIpAddress() throws SocketException {

		for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
					.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()) {
					String ip = Formatter.formatIpAddress(inetAddress
							.hashCode());
					return ip;
				}
			}

		}
		return null;
	}
}
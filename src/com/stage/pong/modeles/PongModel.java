package com.stage.pong.modeles;

import java.net.InetAddress;
import java.util.Observable;


public class PongModel extends Observable {
	private Ball BALL;
	private Terrain TERRAIN;
	private boolean isBallInTerrain;
	private InetAddress IP;
	
	
	public InetAddress getIP() {
		return IP;
	}
	public void setIP(InetAddress iP) {
		IP = iP;
		this.setChanged();
		this.notifyObservers();
	}
	public PongModel(boolean isBallInTerrain) {
		super();
		this.isBallInTerrain = isBallInTerrain;
		this.IP=null;
	}
	public Ball getBALL() {
		return BALL;
	}
	public void setBALL(Ball bALL) {
		this.isBallInTerrain=false;
		BALL = bALL;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void updateFromExt(Ball bALL){
		BALL = bALL;
		BALL.setBALL_SPEED(390.0f);
		changeIT();
	}
	
	private void changeIT(){
		this.isBallInTerrain=true;
		this.setChanged();
		this.notifyObservers();
	}
	public Terrain getTERRAIN() {
		return TERRAIN;
	}
	public void setTERRAIN(Terrain tERRAIN) {
		TERRAIN = tERRAIN;
	}
	public boolean isBallInTerrain() {
		return isBallInTerrain;
	}
	public void setBallInTerrain(boolean isBallInTerrain) {
		this.isBallInTerrain = isBallInTerrain;
	}

}

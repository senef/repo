package com.stage.pong.modeles;

import java.util.Observable;

public class GameScoreManager extends Observable {
	private int SCORE=0;
	private int MISSES=0;
	
	
	
	
	public void incrementScore(){
		this.SCORE ++;
	///	this.setChanged();this.notifyObservers();
	}
	public void incrementMisses(){
		this.MISSES ++;
		//this.setChanged();this.notifyObservers("misse");
	}
	public String getSCORE() {
		return String.valueOf(SCORE);
	}
	public void setSCORE(int sCORE) {
		SCORE = sCORE;
		//this.setChanged();this.notifyObservers("score");
	}
	public String getMISSES() {
		return String.valueOf(MISSES);
	}
	public void setMISSES(int mISSES) {
		MISSES = mISSES;
	}
	
	

}

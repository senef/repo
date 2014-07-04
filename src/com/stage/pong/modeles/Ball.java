package com.stage.pong.modeles;

public class Ball  {
	private float x;
	private float y;
	private float BALL_SPEED;
	private float X_SPEED;
	private float Y_SPEED;


	
	
	public Ball(float x, float y, float bALL_SPEED) {
		super();
		this.x = x;
		this.y = y;
		BALL_SPEED = bALL_SPEED;
	}
	
	
	public Ball(float x, float y) {
		super();
		this.x = x;
		this.y = y;
		this.BALL_SPEED=170.0f;
	}



	public Ball(float x, float y, float x_SPEED, float y_SPEED) {
		super();
		this.x = x;
		this.y = y;
		X_SPEED = x_SPEED;
		Y_SPEED = y_SPEED;
	}


	public float getX_SPEED() {
		return X_SPEED;
	}


	public void setX_SPEED(float x_SPEED) {
		X_SPEED = x_SPEED;
	}


	public float getY_SPEED() {
		return Y_SPEED;
	}


	public void setY_SPEED(float y_SPEED) {
		Y_SPEED = y_SPEED;
	}


	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getBALL_SPEED() {
		return BALL_SPEED;
	}
	public void setBALL_SPEED(float bALL_SPEED) {
		BALL_SPEED = bALL_SPEED;
	}
	
	

}

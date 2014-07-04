package com.stage.pong.controllers;

import java.util.Observable;
import java.util.Observer;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.stage.pong.modeles.Ball;
import com.stage.pong.modeles.GameScoreManager;
import com.stage.pong.modeles.PongModel;
import com.stage.pong.udpconnection.PongCommunication;
import com.stage.pong.views.scenes.PongScene;
import com.stage.pong.views.sprites.PongSprite;

public class PongControllerTwoPlayer extends BaseGameActivity implements Observer,
		IAnalogOnScreenControlListener {

	// constantes
	public final static int CAMERA_LARGEUR = 320;
	public final static int CAMERA_HAUTEUR = 480;
	public static final int MESSAGE_READ = 1;
	public static final int MESSAGE_ASK = 2;
	public static final int MESSAGE_SCORE = 4;
	private int scoreI=0;
	private int missesI=0;

	// variables de communications
	public String remoteIP = null;
	private PongCommunication PC = null;

	// modele
	private static PongModel PM;
	private static GameScoreManager GSM;

	// scene et camera
	private static PongScene maScene;
	public static Camera camera;

	// Controle analogique
	private AnalogOnScreenControl analogOnScreenControl;

	// Textures des controles pour se déplacer
	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	private PhysicsHandler physicsHandler;

	// affichage textuel
	private Font font;
	static ChangeableText missesText;
	static ChangeableText scoreText;
	public String joueur = "";
	static int misses = 0;
	static int score = 0;

	// ballon et raquette de jeu
	private BitmapTextureAtlas paddleTexture;
	private BitmapTextureAtlas ballTexture;
	public static BallSprite ball;
	private TiledTextureRegion ballTextureRegion;
	private TextureRegion paddleTextureRegion;
	private static PongSprite paddle;


	// Handler recoit donnees depuis PongCommunication
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MESSAGE_READ:

				String readBuf = (String) msg.obj;
				String str[] = readBuf.split(",");
				PM.updateFromExt(new Ball(Float.valueOf(str[0]), -Float.valueOf(str[1])));
				break;
				
			case MESSAGE_SCORE:

				String remoteScore = (String) msg.obj;				
				GSM.setSCORE(Integer.valueOf(remoteScore));
				break;

			}
		}

	};
	private BitmapTextureAtlas mTexture;
	private BitmapTextureAtlas mBackgroundTexture;
	private TextureRegion mBackgroundGrassTextureRegion;
	
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PC.stop();
	}
	
	

	@Override
	public Engine onLoadEngine() {
		// reseau
		
		this.remoteIP = getIntent().getExtras().getString("roll");
		this.joueur = getIntent().getExtras().getString("jp");
		GSM= new GameScoreManager();
		GSM.addObserver(this);
		PM = new PongModel(false);
		PM.addObserver(this);

		PC = new PongCommunication(this, this.mHandler);

		// Initialisation de la caméra
		camera = new Camera(0, 0, CAMERA_LARGEUR, CAMERA_HAUTEUR);

		// Initialisation de la scéne du jeu
		maScene = new PongScene();

		// Retourne le moteur de jeu
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(CAMERA_LARGEUR, CAMERA_HAUTEUR),
				camera));
	}

	@Override
	public void onLoadResources() {
		
		// Chargement des textures de la scéne
		//this.maScene.setBackground(new ColorBackground(0, 0, 0));
		LoadResources(getEngine(), this);
		// if(this.remoteIP!=null){
		// PC.startbis(this.remoteIP);}else{
		PC.start();
		// }
	}

	@Override
	public Scene onLoadScene() {
		// Retourne la scene
		this.configureScene();
		return maScene;
	}

	@Override
	public void onLoadComplete() {

	}

	
	@Override
	public void onPauseGame(){
		super.onPauseGame();;
		System.exit(0);
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
if(!arg0.getClass().equals(GameScoreManager.class)){
		if (PM.isBallInTerrain()) {
			ball = new BallSprite(PM.getBALL().getX(), PM.getBALL().getY(),
					ballTextureRegion, this.PC);
			this.maScene.attachChild(ball);
			PM.setBallInTerrain(false);
		} else {

			if (PM.getBALL().getY() < -0.2) {

				this.sendMessage("posi/" + PM.getBALL().getX() + ","
						+ PM.getBALL().getY());
				this.detachBallFromScene();
			}
		}
}else{
	
		if(this.missesI!=Integer.valueOf(GSM.getMISSES())){
			this.missesText.setText("Misses : "+GSM.getMISSES());
			sendMessage("sco/"+GSM.getMISSES());
			this.missesI=Integer.valueOf(GSM.getMISSES());
		}
		
		//if(this.scoreI!=Integer.valueOf(GSM.getMISSES())){
		
		this.scoreText.setText("Score : "+GSM.getSCORE());
		this.scoreI=Integer.valueOf(GSM.getSCORE());
	//	}
		
}
		

	}

	private void sendMessage(String message) {

		// non vide?
		if (message.length() > 0) {

			byte[] send = message.getBytes();

			PC.write(send);

		}
	}
	
	
	public void loadBackground(){
		
        this.mBackgroundTexture = new BitmapTextureAtlas(2048,2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mBackgroundGrassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTexture, this, "gfx/bg.png", 0, 0);
        this.mEngine.getTextureManager().loadTextures(this.mBackgroundTexture);
	}

	// //////////////////////////////////////////////////

	/**
	 * Initialisation de la scéne
	 */
	private void configureScene() {

		// Initialisation du controle analogique
		analogOnScreenControl = new AnalogOnScreenControl(50, CAMERA_HAUTEUR
				- this.mOnScreenControlBaseTextureRegion.getHeight(), camera,
				mOnScreenControlBaseTextureRegion,
				mOnScreenControlKnobTextureRegion, 0.1f, 200, this);

		// Spécifie que le contrôle analogie sera transparent
		analogOnScreenControl.getControlBase().setBlendFunction(
				GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// Valeur de la transparence
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		// Taille du centre
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		// Redimensionnement de la base du controle
		analogOnScreenControl.getControlBase().setScale(0.5f);
		// Redimensionnement du joystick
		analogOnScreenControl.getControlKnob().setScale(0.5f);
		// Rafraichit le controle
		analogOnScreenControl.refreshControlKnobPosition();

		// Ajout du controle à la scéne enfant
		this.maScene.setChildScene(analogOnScreenControl);

		// Initialisation de notre tank
		final float centerX = (CAMERA_LARGEUR - paddleTextureRegion.getWidth()) / 2;
		float centerY = 320;
		paddle = new PongSprite(centerX, centerY, paddleTextureRegion.getWidth()+50,
				paddleTextureRegion.getHeight()-20, paddleTextureRegion);
		// Redimensionne notre tank
		paddle.setScale(0.5f);

		// Ajout de notre tank à la scéne
		this.maScene.attachChild(paddle);
		this.maScene.registerTouchArea(paddle);
		physicsHandler = new PhysicsHandler(paddle);
		paddle.registerUpdateHandler(physicsHandler);

		// box

		missesText = new ChangeableText(130, 415, this.font, "Misses : 0",
				"Misses : XXXXX".length());
		scoreText = new ChangeableText(130, 445, this.font, "Score : 0 ",
				"Score : XXXXX".length());

		missesText.setScale((float) 0.75);
		missesText.setColor(1, 1, 1);
		scoreText.setScale((float) 0.75);
		scoreText.setColor(1, 1, 1);

		this.maScene.attachChild(missesText);
		this.maScene.attachChild(scoreText);

		final Line line1 = new Line(0, 0, 320, 0, 5);
		final Line line2 = new Line(320, 0, 320, 400, 5);
		final Line line3 = new Line(320, 400, 0, 400, 5);
		final Line line4 = new Line(0, 400, 0, 0, 5);

		this.maScene.attachChild(line1);
		this.maScene.attachChild(line2);
		this.maScene.attachChild(line3);
		this.maScene.attachChild(line4);

		// Add ball to scene

		final int ballX = (CAMERA_LARGEUR - this.ballTextureRegion.getWidth()) / 2;
		final int ballY = (CAMERA_HAUTEUR - this.ballTextureRegion.getHeight()) / 2;
		ball = new BallSprite(100, 0, ballTextureRegion, this.PC);

		if (this.joueur.equals("1P")) {
			this.maScene.attachChild(ball);
		}
		
		Sprite bgSprite = new Sprite(0,0, CAMERA_LARGEUR, 400, mBackgroundGrassTextureRegion);
		SpriteBackground background=new SpriteBackground(bgSprite);
		maScene.setBackground(background);

	}

	/**
	 * Chargement des resources
	 * 
	 * @param engine
	 * @param context
	 */
	public void LoadResources(final Engine engine, Context context) {
		this.loadBackground();
		//Chargement des textures de la raquete*t
		paddleTexture = new BitmapTextureAtlas(256,1024,
				TextureOptions.BILINEAR);
		paddleTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(paddleTexture, context, "gfx/pad128.png", 0,
						0);

		// Chargement des textures du contrôle analogique
		mOnScreenControlTexture = new BitmapTextureAtlas(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, context,
						"gfx/onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, context,
						"gfx/onscreen_control_knob.png", 128, 0);

		// Create a ball
		ballTexture = new BitmapTextureAtlas(128, 128);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.ballTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ballTexture, context, "ball32.png", 0, 0,
						1, 1);

		engine.getTextureManager().loadTexture(ballTexture);

		// Chargement des textures dans le texture manager
		engine.getTextureManager().loadTextures(paddleTexture,
				mOnScreenControlTexture, ballTexture);

		// font
		final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256,
				TextureOptions.BILINEAR);
		engine.getTextureManager().loadTexture(fontTexture);
		font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT_BOLD,
				Typeface.BOLD), 24, true, Color.WHITE);
		engine.getFontManager().loadFont(font);

	}

	@Override
	public void onControlChange(BaseOnScreenControl pBaseOnScreenControl,
			float pValueX, float pValueY) {
		physicsHandler.setVelocity(pValueX * 120, pValueY * 0);
		// paddle.setPosition( paddle.getX() , paddle.getY() + (pValueY * 15));

	}

	@Override
	public void onControlClick(AnalogOnScreenControl arg0) {
		// TODO Auto-generated method stub

	}

	private static void detachBallFromScene() {
		// TODO Auto-generated method stub
		maScene.detachChild(ball);

	}

	/************ INNER CLASS ******************/

	private static class BallSprite extends AnimatedSprite {

		private final PhysicsHandler mPhysicsHandler;
		private static final float VELOCITY = 370.0f;

		public BallSprite(final float pX, final float pY,
				final TiledTextureRegion pTextureRegion, PongCommunication PC) {
			super(pX, pY, pTextureRegion);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			// Set differential x & y component for angle other than 45
			this.mPhysicsHandler.setVelocity(VELOCITY, (float) 0.70 * VELOCITY);
		}
		

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {

			PM.setBALL(new Ball(mX, mY, VELOCITY));
		

			if (this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(VELOCITY);
			} else if (this.mX + this.getWidth() > 320) {
				this.mPhysicsHandler.setVelocityX(-VELOCITY);
			}

			if (this.mY < 0) {
				// this.mPhysicsHandler.setVelocityY(VELOCITY);
			} else if (this.mY + this.getHeight() > 400) { // Edge of bounded
															// rectangle
				GSM.incrementMisses();
				
				// At bottom. Restart from the top
				this.setPosition(this.getX() + 10, 10);
				

				this.mPhysicsHandler.setVelocityY(VELOCITY);
			}

			// Check collisions
			if (paddle.collidesWith(this) || this.collidesWith(paddle)) {
				float vx = this.mPhysicsHandler.getVelocityX();
				float vy = this.mPhysicsHandler.getVelocityY();
			/*	if(this.getX()<paddle.getX()-paddle.getWidth()/2 && this.getY()>paddle.getY()-paddle.getHeight()/2){
					this.mPhysicsHandler.setVelocityX(-VELOCITY);
				}
				else if(this.getX()>paddle.getX()+paddle.getWidth()/2 && this.getY()>paddle.getY()-paddle.getHeight()/2){
					this.mPhysicsHandler.setVelocityX(VELOCITY);
				}*/
								this.mPhysicsHandler.setVelocity(-vx, -vy);
				
			}

			super.onManagedUpdate(pSecondsElapsed);
		}

	}
	
	

}

package com.stage.pong.views.sprites;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class PongSprite extends Sprite {

	public PongSprite(float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion) {
		super(pX, pY, pWidth, pHeight, pTextureRegion);
		// TODO Auto-generated constructor stub
	}
	
	
		@Override
		public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, this.getY());
			return true;
		}

}

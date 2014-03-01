package com.projecthawkthorne.client;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureInstance {

	private TextureRegion texture;
	private float x;
	private float y;

	public TextureInstance(TextureRegion texture, float x, float y) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		
		this.texture.flip(false, true);
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public boolean contains(float x2, float y2) {
		return x2 > x 
				&& x2 < x+texture.getRegionWidth()
				&& y2 > y 
				&& y2 < y+texture.getRegionHeight();
	}


}

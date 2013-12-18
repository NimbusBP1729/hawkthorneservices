package com.projecthawkthorne.client.display;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation extends com.badlogic.gdx.graphics.g2d.Animation {

	public Animation(float f, int playMode, TextureRegion... regions) {
		super(f,regions);
		this.setPlayMode(playMode);
		// TODO Auto-generated constructor stub
	}

}

package com.projecthawkthorne.client.display;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.content.nodes.State;

public class NodeSpriteMap {

	public Animation lookUp(String type, String name, State state) {
		Texture texture;
		Animation anim = null;
		if("liquid".equals(type)){
			texture = Assets.loadTexture(Assets.SRC_IMAGES+"water.png");
			anim = new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(texture, 48, 0, 24, 24));
		}
		return anim;

	}

}

package com.projecthawkthorne.client;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CharacterBundle {
	String name;
	TextureRegion texture;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TextureRegion getTexture() {
		return texture;
	}
	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}
}

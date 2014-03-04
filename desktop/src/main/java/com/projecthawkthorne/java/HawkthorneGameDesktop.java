package com.projecthawkthorne.java;

import static com.projecthawkthorne.client.HawkthorneParentGame.HEIGHT;
import static com.projecthawkthorne.client.HawkthorneParentGame.WIDTH;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.projecthawkthorne.client.HawkthorneParentGame;

public class HawkthorneGameDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = WIDTH;
		config.height = HEIGHT;
		new LwjglApplication(new HawkthorneParentGame(), config);
	}
}

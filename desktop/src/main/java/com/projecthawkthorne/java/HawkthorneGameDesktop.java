package com.projecthawkthorne.java;

import static com.projecthawkthorne.client.HawkthorneGame.HEIGHT;
import static com.projecthawkthorne.client.HawkthorneGame.WIDTH;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.projecthawkthorne.client.HawkthorneGame;

public class HawkthorneGameDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = WIDTH;
		config.height = HEIGHT;
		new LwjglApplication(new HawkthorneGame(), config);
	}
}

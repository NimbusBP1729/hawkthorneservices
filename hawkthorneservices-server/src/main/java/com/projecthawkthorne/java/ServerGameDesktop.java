package com.projecthawkthorne.java;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglHeadlessApplication;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class ServerGameDesktop {
	public static void main(String[] args) {
		initGdx();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		new LwjglHeadlessApplication(new HawkthorneServerGame());
		
	}
	
	public static void initGdx() {
		GdxNativesLoader.load();
		Gdx.graphics = mock(Graphics.class);
		Gdx.audio = mock(Audio.class);
		Gdx.gl = mock(GLCommon.class);
		Gdx.gl10 = mock(GL10.class);
		Gdx.gl11 = mock(GL11.class);
		Gdx.gl20 = mock(GL20.class);
		Gdx.input = mock(Input.class);
	}
}

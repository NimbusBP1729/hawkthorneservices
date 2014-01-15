package com.projecthawkthorne.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneParentGame extends Game {
	private SpriteBatch spriteBatch;
	public static Mode MODE;
	public static final String START_LEVEL = "town";
	public String trackedLevel = START_LEVEL;
	protected Player trackedPlayer;
	protected long lastTime = 0;
	private Screen lastScreen;
	
	@Override
	public void create() {
		Assets.load(new AssetManager());
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(new HawkInputProcessor());
		spriteBatch = new SpriteBatch();
		Gamestate.setContext(this);
		this.setScreen(Level.get(START_LEVEL));
	}

	@Override
	public final void render() {
		Timer.updateTimers();
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		this.getScreen().render(dt/1000f);
		spriteBatch.begin();
		((Gamestate) this.getScreen()).draw(spriteBatch);
		spriteBatch.end();
	}
	
	@Override
	public void setScreen(Screen screen){
		this.lastScreen = this.getScreen();
		super.setScreen(screen);
	}

	public void goBack() {
		this.setScreen(this.lastScreen);
	}

}

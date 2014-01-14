package com.projecthawkthorne.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneParentGame extends Game {
	private SpriteBatch spriteBatch;
	private OrthographicCamera cam;
	public static Mode MODE;
	public static final String START_LEVEL = "town";
	public String trackedLevel = START_LEVEL;
	protected Player trackedPlayer;
	protected long lastTime = 0;
	private OrthogonalTiledMapRenderer tmr;
	protected static final boolean IS_Y_DOWN = false;
	
	@Override
	public void create() {
		Assets.load();
		spriteBatch = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(IS_Y_DOWN, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.zoom = 0.5f;
		tmr = new OrthogonalTiledMapRenderer(null,spriteBatch);
		Level.setContext(this);
		this.setScreen(Level.get(START_LEVEL));
	}
	
	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(IS_Y_DOWN, width, height);
	}

	@Override
	public final void render() {
		Timer.updateTimers();
		
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		this.getScreen().render(dt*1000f);
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(cam.combined);
		((Gamestate) this.getScreen()).draw(cam, spriteBatch, tmr);
		spriteBatch.end();
	}

}

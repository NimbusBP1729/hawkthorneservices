package com.projecthawkthorne.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.GenericGamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneGame extends Game {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	
	public static final String START_LEVEL = "town";
	public String trackedLevel = START_LEVEL;
	protected Player trackedPlayer;
	protected long lastTime = 0;
	private Screen lastScreen;
	private HawkthorneUserInterface userInterface;
	
	
	@Override
	public void create() {
		Assets.load(new AssetManager());
		Gdx.input.setCatchBackKey(true);
		userInterface = new HawkthorneUserInterface();
		Gdx.input.setInputProcessor(userInterface);
		Gamestate.setContext(this);

		Level level = Level.get(START_LEVEL);
		this.setScreen(level);
		Level.switchState(level, level.getDoor("main"), Player.getSingleton());
	}

	@Override
	public final void render() {
		Timer.updateTimers();
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;

		//handles all time updates
		this.getScreen().render(dt/1000f);
		this.userInterface.update(dt);
		
		//handles all drawing to the screen
		((Gamestate) this.getScreen()).draw();
		this.userInterface.draw();
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		userInterface.resize(width, height);
	}
	
	@Override
	public void setScreen(Screen screen){
		this.lastScreen = this.getScreen();
		super.setScreen(screen);
	}
	public void setScreen(String screenName){
		setScreen(GenericGamestate.get(screenName));
	}

	public void goBack() {
		this.setScreen(this.lastScreen);
	}

	public HawkthorneUserInterface getControlsOverlay() {
		return userInterface;
	}

}

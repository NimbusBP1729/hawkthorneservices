package com.projecthawkthorne.socket;

import com.badlogic.gdx.ApplicationListener;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;

public abstract class HawkGdxTest implements ApplicationListener{

	public HawkGdxTest(Mode mode) {
		HawkthorneGame.MODE = mode;
	}
	
	private boolean isComplete = false;

	public abstract Object getOutput();	

	@Override
	public void create() {
		isComplete = false;
		runTest();
		isComplete = true;
	}

	public abstract void runTest();

	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}

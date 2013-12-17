package com.projecthawkthorne.html;

import com.projecthawkthorne.core.HawkthorneGame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HawkthorneGameHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new HawkthorneGame();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}

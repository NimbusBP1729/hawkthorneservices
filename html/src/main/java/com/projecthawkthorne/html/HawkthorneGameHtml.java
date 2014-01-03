package com.projecthawkthorne.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;

public class HawkthorneGameHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener() {
		return new HawkthorneGame(Mode.CLIENT);
	}

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(480, 320);
	}
}

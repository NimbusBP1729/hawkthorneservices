package com.projecthawkthorne.client;

import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;

/**
 * Note: should really be named HawkthorneClientGame, but I didn't want to cause
 * additional breakage to other modules
 * @author Pat
 *
 */
public class HawkthorneGame extends HawkthorneParentGame {
	protected final Player trackedPlayer;
	
	public HawkthorneGame(){
		HawkthorneGame.MODE = Mode.CLIENT;
		trackedPlayer = Player.getSingleton();
	}

	public HawkthorneGame(String[] args) {
		this();
		if(args.length > 0)
			Player.getSingleton().setUsername(args[0]);
	}

	@Override
	public void create() {
		super.create();
		Level level = Level.get(START_LEVEL);
		Level.switchState(level, level.getDoor("main"), Player.getSingleton());
	}

}

package com.projecthawkthorne.gamestate;

import java.util.HashMap;
import java.util.Map;

import com.projecthawkthorne.content.GameKeys;

public abstract class GenericGamestate extends Gamestate {
	private Map<GameKeys, Boolean> keyMap = new HashMap<GameKeys, Boolean>();

	public final void setIsKeyDown(GameKeys button, boolean b) {
		this.keyMap.put(button, Boolean.valueOf(b));
	}

	public final boolean getIsKeyDown(GameKeys button) {
		Boolean b = keyMap.get(button);
		return b == null ? false : b;
	}

	public abstract void keypressed(GameKeys gk);

	public abstract void keyreleased(GameKeys gk);

	public String getSoundtrack() {
		return "opening";
	}

}

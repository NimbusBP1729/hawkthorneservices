package com.projecthawkthorne.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public abstract class GenericGamestate extends Gamestate {
	private Map<GameKeys, Boolean> keyMap = new HashMap<GameKeys, Boolean>();
	protected List<RadioButtonGroup> objects = new ArrayList<RadioButtonGroup>();

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

	/**
	 * should return GamestateElemets in the futue
	 * 
	 * @return
	 */
	public List<RadioButtonGroup> getObjects() {
		return objects;
	}

}

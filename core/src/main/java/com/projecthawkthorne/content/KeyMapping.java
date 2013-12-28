package com.projecthawkthorne.content;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;

public class KeyMapping {
	private static Map<GameKeys, Integer> constants = new HashMap<GameKeys, Integer>();
	static {
		constants.put(GameKeys.LEFT, Keys.LEFT);
		constants.put(GameKeys.RIGHT, Keys.RIGHT);
		constants.put(GameKeys.UP, Keys.UP);
		constants.put(GameKeys.DOWN, Keys.DOWN);
		constants.put(GameKeys.JUMP, Keys.SPACE);
		constants.put(GameKeys.ATTACK, Keys.A);
		constants.put(GameKeys.INTERACT, Keys.V);
		constants.put(GameKeys.START, Keys.ESCAPE);
		constants.put(GameKeys.SELECT, Keys.ENTER);
	}

	public static Integer gameKeyToInt(GameKeys gk) {
		return constants.get(gk);
	}
}

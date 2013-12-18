package com.projecthawkthorne.gamestate;

import java.util.ArrayList;
import java.util.List;

import com.projecthawkthorne.gamestate.elements.GamestateElement;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class Introduction extends Gamestate {

	private List<GamestateElement> objects = new ArrayList<GamestateElement>();
	private Levels levels = Levels.getSingleton();

	public Introduction() {
		objects.add(new RadioButtonGroup("multiplayer", "singleplayer"));
		// objects.add(new SelectionButton(levels.get("town")));
	}

	@Override
	public String getName() {
		return "introduction";
	}

	@Override
	public void update() {
		for (GamestateElement obj : objects) {

		}

	}

}

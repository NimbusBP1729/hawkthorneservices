package com.projecthawkthorne.gamestate;

import java.util.ArrayList;
import java.util.List;

import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.gamestate.elements.GamestateElement;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class Introduction extends GenericGamestate {

	private List<GamestateElement> objects = new ArrayList<GamestateElement>();

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

	@Override
	public void keypressed(GameKeys gk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyreleased(GameKeys gk) {
		// TODO Auto-generated method stub

	}

}

package com.projecthawkthorne.gamestate;

import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.gamestate.elements.GamestateElement;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class Introduction extends GenericGamestate {

	public Introduction() {
		RadioButtonGroup rbg = new RadioButtonGroup(200, 130, 200, 200,
				"multiplayer", "singleplayer");
		objects.add(rbg);
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

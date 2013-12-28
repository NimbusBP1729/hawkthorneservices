package com.projecthawkthorne.gamestate;

import com.badlogic.gdx.Gdx;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.gamestate.elements.GamestateElement;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class Introduction extends GenericGamestate {

	public Introduction() {
		int width = Gdx.graphics.getWidth() / 2;
		int height = Gdx.graphics.getHeight() / 2;
		RadioButtonGroup rbg = new RadioButtonGroup(0, 0, width - 10,
				height - 10, "multiplayer", "singleplayer");
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

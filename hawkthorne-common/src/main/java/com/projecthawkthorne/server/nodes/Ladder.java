package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;

public class Ladder extends Climbable {

	public Ladder(RectangleMapObject obj, Gamestate level) {
		super(obj, level);
		Level lvl = (Level) level;
		lvl.getCollider().setPassive(bb);
	}

	@Override
	protected void updateVelocity(long dt) {
	}

}

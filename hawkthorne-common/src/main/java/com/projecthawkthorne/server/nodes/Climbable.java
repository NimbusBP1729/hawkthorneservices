package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.server.Game;
import com.projecthawkthorne.server.Keys;
import com.projecthawkthorne.server.Player;

public abstract class Climbable extends Node {

	public float speed = 10 * Game.step;

	public Climbable(RectangleMapObject obj, Gamestate level) {
		super(obj, level);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void collide(Node node) {
		if (node instanceof Player) {
			Player h = (Player) node;
			if (h.isClimbing()
					&& (h.getKeyDown(Keys.LEFT) || h.getKeyDown(Keys.RIGHT))) {
				h.unClimb(this);
			}
			h.setClimbable(this);
		}
	}

	@Override
	protected void collideEnd(Node node) {
		if (node instanceof Humanoid) {
			Humanoid h = (Humanoid) node;
			if (h.isClimbing()) {
				// FIXME: this should use the previous state set
				h.unClimb(this);
			}
			// FIXME: this may not work if multiple colliders touch each other
			h.setClimbable(null);
		}
	}

}

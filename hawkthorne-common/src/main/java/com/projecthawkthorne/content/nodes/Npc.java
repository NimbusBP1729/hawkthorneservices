package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;

public abstract class Npc extends Humanoid {

	public Npc(RectangleMapObject obj, Level level) {
		super(obj, level);
	}

	@Override
	protected void updateVelocity(long dt) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void collide(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void collideEnd(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void floorPushback(Bound floor, float newY) {
		// TODO Auto-generated method stub

	}

}

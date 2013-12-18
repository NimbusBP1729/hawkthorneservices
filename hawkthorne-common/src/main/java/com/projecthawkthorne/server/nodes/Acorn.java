package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;

public class Acorn extends Enemy {

	public Acorn(RectangleMapObject obj, Level level) {
		super(obj, level);
		this.maxx = this.x + 48;
		this.minx = this.x - 48;
		this.behaviors.add(Behavior.Pacing);
		this.behaviors.add(Behavior.Raging);
		this.behaviors.add(Behavior.GravityObeying);

	}

	@Override
	protected void updateVelocity(long dt) {
		super.updateVelocity(dt);
	}
}

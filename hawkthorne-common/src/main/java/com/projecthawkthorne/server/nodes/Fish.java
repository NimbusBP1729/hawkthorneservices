package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;

public class Fish extends Enemy {
	public Fish(RectangleMapObject obj, Level level) {
		super(obj, level);
		this.behaviors.add(Behavior.GravityObeying);
		this.behaviors.add(Behavior.Jumping);
	}
}

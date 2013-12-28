package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;

public class Frog extends Enemy {

	public Frog(RectangleMapObject obj, Level level) {
		super(obj, level);
		this.behaviors.add(Behavior.GravityObeying);
		this.behaviors.add(Behavior.Jumping);
	}

}

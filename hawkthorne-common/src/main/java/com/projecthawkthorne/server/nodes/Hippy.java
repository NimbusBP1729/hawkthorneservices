package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;

public class Hippy extends Enemy {

	public Hippy(RectangleMapObject obj, Level level) {
		super(obj, level);
		this.width = 48;
		this.height = 48;
		this.bb.setWidth(30);
		this.bb.setHeight(24);
		this.bboxOffsetY = 24;

		this.behaviors.add(Behavior.Following);
		this.followSpeed = 30;

		this.behaviors.add(Behavior.GravityObeying);
	}

}

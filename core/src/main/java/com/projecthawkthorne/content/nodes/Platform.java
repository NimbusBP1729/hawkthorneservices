/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import java.util.Iterator;

import com.badlogic.gdx.maps.MapObject;
import com.projecthawkthorne.content.FloorCollidable;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;

/**
 * differs from floors because you can drop from it and it sends no
 * wallPushbacks
 * 
 * @author Patrick
 */
public class Platform extends Node {

	private boolean canDrop = true;

	public Platform(MapObject t, Level level) {
 		super(t, level);
		canDrop = !"false".equals(t.getProperties().get("drop",String.class));
		level.getCollider().setPassive(this.bb);
	}

	@Override
	protected void collide(Node node) {
		if (!(node instanceof Humanoid)) {
			return;
		}
		Player player = (Player) node;
		if (player.isDroppingFrom(this)) {
			return;
		}
		if(player.velocityY > 0){
			player.dropFromPlatform(this);
			return;
		}
		float[] nodeCorners = Bound.FLOAT_ARRAY;
		node.bb.bbox(nodeCorners);
		float nodeLeft = nodeCorners[0];
		float nodeRight = nodeCorners[2];
		float nodeTop = nodeCorners[1];

		FloorCollidable floorPushable = (FloorCollidable) node;
		float y1 = this.bb.getLargestY(nodeLeft);
		float y2 = this.bb.getLargestY(nodeRight);
		float floorTop = Math.max(y1, y2);
		if(floorTop - nodeTop < -12){
			floorPushable.floorPushback(this.bb, floorTop);
		}
	}

	public static float min(float[] yVals) {
		if (yVals == null) {
			throw new IllegalArgumentException("null array");
		} else if (yVals.length == 0) {
			throw new IllegalArgumentException("empty array");
		}
		float minimum = Float.MAX_VALUE;
		for (int i = 0; i < yVals.length; i++) {
			minimum = yVals[i] < minimum ? yVals[i] : minimum;
		}
		return minimum;
	}

	@Override
	protected void updateVelocity(long dt) {
		Iterator<Player> it = this.playersTouched.iterator();
		while (it.hasNext()) {
			Player player = it.next();
			if (player.getIsKeyDown(GameKeys.DOWN)) {
				player.down_dt = 0;
			} else {
				player.down_dt = player.down_dt + dt;
			}
		}
	}

	@Override
	protected void collideEnd(Node node) {
		if(node instanceof Player){
			Player player = (Player) node;
			if(player.isDroppingFrom(this))
				player.dropFromPlatform(null);
		}
	}

	@Override
	public boolean playerKeypressed(GameKeys button, Player player) {
		if (this.canDrop && button == GameKeys.DOWN && player.down_dt > 0
				&& player.down_dt < 150) {
			player.dropFromPlatform(this);
			return true;
		}

		return false;
	}
}

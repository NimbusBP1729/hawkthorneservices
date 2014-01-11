/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import java.util.Iterator;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
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

	public Platform(RectangleMapObject obj, Level level) {
		super(obj, level);
		level.getCollider().setPassive(this.bb);
	}

	@Override
	protected void collide(Node playerNodeqe) {
		if (!(playerNodeqe instanceof Humanoid)) {
			return;
		}
		Player player = (Player) playerNodeqe;
		if (player.isDroppingFrom(this)) {
			return;
		}
		if (player.velocityY < 0) {
			float[] playerCorners = Bound.FLOAT_ARRAY;
			player.bb.bbox(playerCorners);
			float playerBottom = playerCorners[3];

			float[] floorCorners = Bound.FLOAT_ARRAY;
			this.bb.bbox(floorCorners);
			float floorTop = floorCorners[1];

			player.dropFromPlatform(null);
			float fd = floorTop - playerBottom;
			if (fd > 24) {
				return;
			} else if (fd * player.velocityY > -20) {
				player.floorPushback(this.bb, floorTop);
			}
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
		// TODO Auto-generated method stub

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

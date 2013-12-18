/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server.nodes;

import java.util.Iterator;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.server.FloorCollidable;
import com.projecthawkthorne.server.Keys;
import com.projecthawkthorne.server.Player;

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
	protected void collide(Node playerNode) {
		if (playerNode.bb == null
				|| (playerNode instanceof Humanoid && ((Humanoid) playerNode)
						.isDroppingFrom(this))) {
			return;
		}
		if (playerNode instanceof FloorCollidable && playerNode.velocityY > 0) {
			FloorCollidable player = (FloorCollidable) playerNode;
			float[] playerCorners = new float[4];
			playerNode.bb.bbox(playerCorners);
			float playerTop = playerCorners[1];
			float playerBottom = playerCorners[3];

			float[] yVals = new float[12];
			float diff = playerCorners[2] - playerCorners[0];
			for (int i = 0; i < yVals.length; i++) {
				yVals[i] = this.bb.getSmallestY(playerCorners[0] + i * diff
						/ 12);
			}
			float floorTop = min(yVals);
			// temporary lazy floor bottom
			float[] floorCorners = new float[4];
			this.bb.bbox(floorCorners);

			if (player instanceof Player) {
				Player plyr = (Player) player;
				plyr.dropFromPlatform(null);
				if ((floorTop - playerBottom) > -100 * plyr.velocityY) {
					player.floorPushback(this.bb, floorTop);
				}

			} else {
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
			if (player.getKeyDown(Keys.DOWN)) {
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
	public boolean playerKeypressed(Keys button, Player player) {
		if (this.canDrop && button == Keys.DOWN && player.down_dt > 0
				&& player.down_dt < 150) {
			player.dropFromPlatform(this);
			return true;
		}

		return false;
	}
}

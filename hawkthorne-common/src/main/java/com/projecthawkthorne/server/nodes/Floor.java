/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.server.FloorCollidable;

/**
 * 
 * @author Patrick
 */
public class Floor extends Node {

	public Floor(RectangleMapObject obj, Level level) {
		super(obj, level);
		level.getCollider().setPassive(this.bb);
	}

	@Override
	protected void collide(Node node) {
		if (node.bb == null) {
			return;
		}

		if (node instanceof FloorCollidable) {
			FloorCollidable floorPushable = (FloorCollidable) node;
			float[] floorCorners = new float[4];
			this.bb.bbox(floorCorners);
			float[] nodeCorners = new float[4];
			node.bb.bbox(nodeCorners);
			// float y1 = this.bb.getSmallestY(nodeCorners[0]);
			// float y2 = this.bb.getSmallestY(nodeCorners[2]);
			// float floorY = Math.min(y1, y2);

			// TODO: use getSmallestY and getLargestY instead
			float floorLeft = floorCorners[0];
			float floorTop = floorCorners[1];
			float floorRight = floorCorners[2];
			float floorBottom = floorCorners[3];

			float nodeLeft = nodeCorners[0];
			float nodeTop = nodeCorners[1];
			float nodeRight = nodeCorners[2];
			float nodeBottom = nodeCorners[3];

			float ceilingY = floorBottom;

			float fd = nodeBottom - floorTop; // must be positive for
												// floorpushback
			float cd = floorBottom - nodeTop; // must be positive for
												// ceilingpushback'
			float wallBuffer = 1;
			float floorBuffer = 10;
			boolean isBetweenWall = (floorBuffer < floorBottom - nodeTop && nodeBottom
					- floorTop > floorBuffer);

			// wall on right side of node
			if (0 < (nodeRight - floorLeft) && (nodeRight - floorLeft) < 20
					&& isBetweenWall) {
				floorPushable.wallPushback(this.bb, floorLeft - wallBuffer,
						true);
				return;
			}

			// wall on left side of node
			else if (0 > (nodeLeft - floorRight)
					&& (nodeLeft - floorRight) > -20 && isBetweenWall) {
				floorPushable.wallPushback(this.bb, floorRight + wallBuffer,
						false);
				return;
			}

			if (fd > 0 && cd > 0) {
				if (fd < cd) {
					floorPushable.floorPushback(this.bb, floorTop);
				} else {
					floorPushable.ceilingPushback(this.bb, ceilingY);
				}
			} else if (fd > 0) {
				floorPushable.floorPushback(this.bb, floorTop);
			} else if (cd > 0) {
				floorPushable.ceilingPushback(this.bb, ceilingY);
			}

		}
		if (node instanceof Humanoid) {
			Humanoid p = (Humanoid) node;
			p.dropFromPlatform(null);
		}
	}

	@Override
	protected void updateVelocity(long dt) {
	}

	@Override
	protected void collideEnd(Node node) {
		// TODO Auto-generated method stub

	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.FloorCollidable;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;

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
			float[] floorCorners = Bound.FLOAT_ARRAY;
			this.bb.bbox(floorCorners);
			// float y1 = this.bb.getSmallestY(nodeCorners[0]);
			// float y2 = this.bb.getSmallestY(nodeCorners[2]);
			// float floorY = Math.min(y1, y2);

			// TODO: use getSmallestY and getLargestY instead
			float floorLeft = floorCorners[0];
			float floorTop = floorCorners[1];
			float floorRight = floorCorners[2];
			float floorBottom = floorCorners[3];

			float[] nodeCorners = Bound.FLOAT_ARRAY;
			node.bb.bbox(nodeCorners);
			float nodeLeft = nodeCorners[0];
			float nodeTop = nodeCorners[1];
			float nodeRight = nodeCorners[2];
			float nodeBottom = nodeCorners[3];

			float wallBuffer = 1;
			boolean onTop = nodeBottom < floorTop && nodeTop > floorTop;
			boolean onBottom = nodeBottom < floorBottom && nodeTop > floorBottom;
			boolean onRight = nodeLeft < floorRight && nodeRight > floorRight;
			boolean onLeft = nodeLeft < floorLeft && nodeRight > floorLeft;
			
			if(onTop){
				floorPushable.floorPushback(this.bb, floorTop);
			}else if(onRight){
				floorPushable.wallPushback(this.bb
						, floorRight + wallBuffer,
						false);
			}else if(onLeft){
				floorPushable.wallPushback(this.bb
						, floorLeft - wallBuffer,
						true);
			}else if(onBottom){
				floorPushable.ceilingPushback(this.bb, floorBottom);
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

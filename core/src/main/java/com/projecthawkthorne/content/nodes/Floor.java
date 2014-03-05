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
			float topDiff = nodeBottom-floorTop;
			float bottomDiff = floorBottom - nodeTop;
			float rightDiff = floorLeft - nodeRight;
			float leftDiff = nodeLeft - floorRight;

			if(topDiff <0 && topDiff > min(rightDiff, leftDiff, bottomDiff)){
				floorPushable.floorPushback(this.bb, floorTop);
			}else if(bottomDiff < 0 && bottomDiff > min(rightDiff, leftDiff, topDiff)){
				floorPushable.ceilingPushback(this.bb, floorBottom);
			}else if(rightDiff < 0 && rightDiff > min(bottomDiff, leftDiff, topDiff)){
				floorPushable.wallPushback(this.bb
						, floorLeft - wallBuffer,
						true);
			}else if(leftDiff < 0 && leftDiff > min(bottomDiff, rightDiff, topDiff)){
				floorPushable.wallPushback(this.bb
						, floorRight + wallBuffer,
						false);
			}else{
				System.out.println("uh-oh");
			}
		}
		if (node instanceof Humanoid) {
			Humanoid p = (Humanoid) node;
			p.dropFromPlatform(null);
		}
	}

	private float min(float val1, float val2, float val3) {
		
		float res;
		if(val1 >= 0 && val2 >= 0 && val3 >= 0){
			res = -Float.MAX_VALUE;
		}else if(val1 >= 0 && val2 >= 0){
			res = val3;
		}else if(val1 >= 0 && val3 >= 0){
			res = val2;
		}else if(val2 >= 0 && val3 >= 0){
			res = val1;
		}else if(val1 >= 0){
			res = Math.max(val2, val3);
		}else if(val2 >= 0){
			res = Math.max(val1, val3);
		}else if(val3 >= 0){
			res = Math.max(val1, val2);
		}else{
			res = Math.max(val1,Math.max(val2, val3));
		}
		return res;
	}

	@Override
	protected void updateVelocity(long dt) {
	}

	@Override
	protected void collideEnd(Node node) {
		// TODO Auto-generated method stub

	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server;

import com.projecthawkthorne.hardoncollider.Bound;

/**
 * 
 * @author Patrick
 */
public interface FloorCollidable {
	public void floorPushback(Bound floor, float newY);

	public void wallPushback(Bound bb, float newX, boolean onRight);

	public void ceilingPushback(Bound bb, float ceilingY);
}

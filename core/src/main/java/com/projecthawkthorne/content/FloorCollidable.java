/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

import com.projecthawkthorne.hardoncollider.Bound;

/**
 * 
 * @author Patrick
 */
public interface FloorCollidable {
	public void floorPushback(Bound floor, float newY);

	public void wallPushback(Bound bb, float newX, boolean wallToTheRight);

	public void ceilingPushback(Bound bb, float ceilingY);
}

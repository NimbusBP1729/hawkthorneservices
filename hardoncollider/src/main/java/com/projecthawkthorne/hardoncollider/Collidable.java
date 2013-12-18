package com.projecthawkthorne.hardoncollider;

import java.util.HashSet;
import java.util.Set;

/**
 * all objects that have an associated Bound should extend this class
 * 
 * @author Patrick
 * 
 */
public abstract class Collidable {
	/** list of the non-player nodes this node is touching */
	protected Set<Collidable> collisionList = new HashSet<Collidable>();

	/**
	 * returns a set of nodes that this is touching
	 * 
	 * @return the list of items you're currently colliding with
	 */
	public Set<Collidable> getCollisionList() {
		return collisionList;
	}

	// TODO: add mtv_x and mtv_y i.e. the offsets indicating how much
	// the 'node' needs to move to no longer collide
	/**
	 * called when this node collides with another node
	 * 
	 * @param node
	 *            he colliding node
	 */
	public void onCollision(Collidable node) {
		this.collisionList.add(node);
		this.collision(node);
	}

	/**
	 * the implementing class uses this method to decide what it wants to do
	 * when a collision happens
	 * 
	 * @param node
	 *            the node that is collided with
	 */
	protected abstract void collision(Collidable node);

	/**
	 * called when this node stops colliding with another node
	 * 
	 * @param node
	 *            the node that is no longer collided with
	 */
	public void onCollisionEnd(Collidable node) {
		this.collisionList.remove(node);
		this.collisionEnd(node);
	}

	/**
	 * the implementing class uses this method to decide what it wants to do
	 * when a collision ends
	 * 
	 * @param node
	 *            the node that is no longer collided with
	 */
	protected abstract void collisionEnd(Collidable node);
}

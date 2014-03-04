/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.hardoncollider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * the collision detection class
 * 
 * @author Patrick
 */
public class Collider {
	/** the list of all shapes */
	private List<Bound> shapeMap;
	/** the list of active shapes */
	private List<Bound> actives;
	/** the list of passive shapes */
	private List<Bound> passives;
	/** the list of ghost shapes */
	private List<Bound> ghosts;
	/** long indicating how frequently we should check for collisions */
	private long updateThreshold = 0;
	/** the last update time */
	private long lastUpdateTime = 0;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	public Collider() {
		this.init();
	}

	/**
	 * sets the threshold determining how frequently we check for more
	 * collisions
	 * 
	 * @param thresh
	 *            the new update threshold
	 */
	public void setUpdateThreshold(long thresh) {
		this.updateThreshold = thresh;
	}

	/**
	 * returns true if the two bounds have intersected
	 * 
	 * @param a
	 *            a bound
	 * @param b
	 *            another bound
	 * @return true if the two bounds have intersected
	 */
	public static boolean intersects(Bound a, Bound b) {
		if (a == null || b == null) {
			throw new UnsupportedOperationException(
					"bound intersection cannot compare nulls");
		} else if (a instanceof Rectangle && b instanceof Rectangle) {
			Rectangle r1 = (Rectangle) a;
			Rectangle r2 = (Rectangle) b;
			return Rectangle.intersects(r1, r2);
		} else if (a instanceof Polygon && b instanceof Rectangle) {
			Polygon p1 = (Polygon) a;
			Rectangle r2 = (Rectangle) b;
			return Polygon.intersects(p1, r2);
		} else if (b instanceof Polygon && a instanceof Rectangle) {
			Polygon p1 = (Polygon) b;
			Rectangle r2 = (Rectangle) a;
			return Polygon.intersects(p1, r2);
		} else {
			throw new UnsupportedOperationException("invalid argument type:"
					+ a.getClass() + "," + b.getClass());
		}
	}
	
	/**
	 * returns true if the two objects collide
	 * 
	 * @param a
	 *            a polygon
	 * @param b
	 *            a rectangle
	 * @return true if the two objects collide
	 */
	private static boolean intersects(Polygon a, Rectangle b) {
		return Collider.intersects(b, a);
	}

	/**
	 * initializes the collider
	 */
	public void init() {
		shapeMap = new ArrayList<Bound>();
		actives = new ArrayList<Bound>();
		passives = new ArrayList<Bound>();
		ghosts = new ArrayList<Bound>();
	}

	/**
	 * adds a new bound to the collider by default, it's set to active.
	 * 
	 * @param s
	 *            the bound to add
	 * @return true if the bounding box did not previously exist
	 */
	public boolean addBox(Bound s) {
		if (shapeMap.contains(s)) {
			return false;
		}
		shapeMap.add(s);
		actives.add(s);
		return true;
	}

	/**
	 * removes a bounding box entirely from the collider
	 * 
	 * @param s
	 *            the bounding box
	 * @return true if the bounding box existed in the collider
	 */
	public boolean removeBox(Bound s) {
		if (!shapeMap.contains(s)) {
			return false;
		}
		shapeMap.remove(s);
		actives.remove(s);
		passives.remove(s);
		ghosts.remove(s);
		return true;
	}

	/**
	 * sets the bounding box to passive. Collisions with passive bounding boxes
	 * are only detected if they collide with an active bounding box <br>
	 * i.e. 2 passives won't know they've collided with each other
	 * 
	 * @param s
	 */
	public void setPassive(Bound s) {
		if (!shapeMap.contains(s)){
			throw new UnsupportedOperationException();
		}else if(ghosts.contains(s)) {
			return;
		}
		
		if (passives.contains(s)) {
			return;
		}
		passives.add(s);
		actives.remove(s);
	}

	/**
	 * sets the bound to active unless it's a ghost <br>
	 * active bounding boxes check all other boxes for collisions
	 * 
	 * @param s
	 *            the bounding box
	 */
	public void setActive(Bound s) {
		if (!shapeMap.contains(s)){
			throw new UnsupportedOperationException();
		}else if(ghosts.contains(s)) {
			return;
		}
		if (actives.contains(s)) {
			return;
		}
		actives.add(s);
		passives.remove(s);
	}

	/**
	 * sets the bounding box to ignore all collisions
	 * 
	 * @param s
	 *            the bounding box
	 */
	public void setGhost(Bound s) {
		if (!shapeMap.contains(s)) {
			return;
		}

		if (ghosts.contains(s)) {
			return;
		}
		ghosts.add(s);
	}

	/**
	 * makes the bounding box able to detect collisions again
	 * 
	 * @param s
	 *            the bounding box
	 */
	public void setSolid(Bound s) {
		if (!shapeMap.contains(s)) {
			return;
		}
		ghosts.remove(s);
	}

	/**
	 * check new collisions and call onCollision or onCollisionEnd accordingly <br>
	 * new collisions are checked only if the elapsed time is greater than
	 * update threshold
	 */
	public void update() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdateTime < updateThreshold) {
			return;
		} else {
			lastUpdateTime = currentTime;
		}
		for (int i = 0; i < actives.size(); i++) {
			Bound a = actives.get(i);
			Collidable aNode = a.getUserData();
			for (int j = i + 1; j < actives.size(); j++) {
				Bound b = actives.get(j);
				Collidable bNode = b.getUserData();
				if (Collider.intersects(a, b)) {
					aNode.onCollision(bNode);
					bNode.onCollision(aNode);
				} else if (aNode.getCollisionList().contains(bNode)) {
					aNode.onCollisionEnd(bNode);
					bNode.onCollisionEnd(aNode);
				}
			}
			for (int j = 0; j < passives.size(); j++) {
				Bound b = passives.get(j);
				Collidable bNode = b.getUserData();
				if (Collider.intersects(a, b)) {
					aNode.onCollision(bNode);
					bNode.onCollision(aNode);
				} else if (aNode.getCollisionList().contains(bNode)) {
					aNode.onCollisionEnd(bNode);
					bNode.onCollisionEnd(aNode);
				}
			}
		}
	}

	/**
	 * returns an iterator for the bounding boxes handled by this collider
	 * 
	 * @return an iterator for the bounding boxes handled by this collider
	 */
	public Iterator<Bound> getBoxes() {
		return shapeMap.iterator();
	}

	/**
	 * This method should exist as an overridden
	 * method in a collider for libgdx
	 * @param batch
	 */
	public void draw(OrthographicCamera cam) {
		Gdx.gl.glEnable(GL30.GL_BLEND);
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeType.Line);
		for(Bound bound: shapeMap){
			if(ghosts.contains(bound)){
				shapeRenderer.setColor(0, 0, 1, 0.35f);
			}else if(passives.contains(bound)){
				shapeRenderer.setColor(0, 1, 0, 0.35f);
			}else if(actives.contains(bound)){
				shapeRenderer.setColor(1, 0, 0, 0.35f);
			}else{
				shapeRenderer.setColor(0, 0, 0, 0.35f);
			}
			if (bound instanceof Rectangle) {
				float x = bound.getX();
				float y = bound.getY();
				float width = bound.getWidth();
				float height = bound.getHeight();
				shapeRenderer.rect(x, y, width, height);
			}else if(bound instanceof Polygon){
				Polygon pb = (Polygon) bound;
				shapeRenderer.polygon(pb.getVertices());
			}else{
				Gdx.app.error("incorrect type", bound.getClass().getName());
			}
		}
		shapeRenderer.end();
		shapeRenderer.setColor(Color.WHITE);
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.hardoncollider;

/**
 * an arbitrary rectangular bound
 * 
 * @author Patrick
 */
class Rectangle extends Bound {

	float x;
	float y;
	float width;
	float height;

	public Rectangle(float x, float y, float bbox_width, float bbox_height) {
		this.x = x;
		this.y = y;
		this.width = bbox_width;
		this.height = bbox_height;
	}

	/**
	 * returns true if the two rectangles intersect
	 * 
	 * @param a
	 *            a rectangle
	 * @param b
	 *            another rectangle
	 * @return true if the two rectangles intersect
	 */
	static boolean intersects(Rectangle a, Rectangle b) {
		return !(b.x > (a.x + a.width) || (b.x + b.width) < a.x
				|| b.y > (a.y + a.height) || (b.y + b.height) < a.y);
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * returns the leftmost position of this rectangle
	 * 
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * sets the left most position of the rectangle
	 * 
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * returns the topmost position of this rectangle
	 * 
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * sets the topmost position of this rectangle
	 * 
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public void bbox(float[] corners) {
		if (corners == null) {
			throw new UnsupportedOperationException("input array is null");
		} else if (corners.length != 4) {
			throw new UnsupportedOperationException(
					"incorrect array size: 4 required");
		}

		corners[0] = this.x;
		corners[1] = this.y;
		corners[2] = this.x + this.width;
		corners[3] = this.y + this.height;
	}

	@Override
	public float getSmallestY(float xVal) {
		float[] corners = new float[4];
		this.bbox(corners);
		return corners[1];
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

	}

}

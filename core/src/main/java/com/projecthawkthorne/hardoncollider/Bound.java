package com.projecthawkthorne.hardoncollider;

/**
 * a generic bounding region
 * 
 * @author Patrick
 * 
 */
public abstract class Bound {
	private Collidable node;

	/**
	 * create a rectangular bound
	 * 
	 * @param x
	 *            the smallest x contained
	 * @param y
	 *            the smallest y contained
	 * @param w
	 *            the width
	 * @param h
	 *            the high
	 * @return the bound
	 */
	public static Bound create(float x, float y, float w, float h) {
		return new Rectangle(x, y, w, h);
	}

	/**
	 * set the node that this bounding box represents
	 * 
	 * @param n
	 */
	public final void setUserData(Collidable n) {
		this.node = n;
	}

	/**
	 * retrieves the node this bound refers to
	 * 
	 * @return the node that the bounding box represent
	 */
	public final Collidable getUserData() {
		return node;
	}

	/**
	 * results in corners={minX,minY,maxX,maxY}
	 * 
	 * @param corners
	 */
	public abstract void bbox(float[] corners);

	/**
	 * moves the object so that the rectangle enclosing it has its leftmost edge
	 * at x = newX and its uppermost edge at y = newY
	 * 
	 * @param newX
	 * @param newY
	 */
	public void moveTo(float newX, float newY) {
		setX(newX);
		setY(newY);
	}

	/**
	 * moves the object so that the rectangle enclosing it has its leftmost edge
	 * at x = newX
	 * 
	 * @param newX
	 */
	public abstract void setX(float newX);

	/**
	 * moves the object so that the rectangle enclosing it has its uppermost
	 * edge at y = newY
	 * 
	 * @param newY
	 */
	public abstract void setY(float newY);

	/**
	 * returns the smallest y value within this bound at location x = xVal<br>
	 * in other words, the minimum of the intersection of <br>
	 * vertical line at xVal and the bounded region <br>
	 * 
	 * @param xVal
	 * @return smallest y-value in the bound intersecting <br>
	 *         the vertical line x=xVal
	 */
	public abstract float getSmallestY(float xVal);

	/**
	 * scales the width to be of size newWidth <br>
	 * the leftmost x position remains the same
	 * 
	 * @param newWidth
	 *            the new width of the bound
	 */
	public abstract void setWidth(float newWidth);

	/**
	 * scales the height to be of size newHeight <br>
	 * the topmost y position remains the same
	 * 
	 * @param newHeight
	 *            the new height of the bound
	 */
	public abstract void setHeight(float newHeight);

	/**
	 * returns the width
	 * 
	 * @return the width
	 */
	public abstract float getWidth();

	/**
	 * returns the width
	 * 
	 * @return the width
	 */
	public abstract float getHeight();

	private float[] tmp = new float[4];

	public float getY() {
		this.bbox(tmp);
		return tmp[1];
	}

	public float getX() {
		this.bbox(tmp);
		return tmp[0];
	}

	/**
	 * increments the width by 1 unit
	 */
	public void incWidth() {
		this.setWidth(getWidth() + 2);
	}

	/**
	 * increments the height by 1 unit
	 */
	public void incHeight() {
		this.setHeight(getHeight() + 2);
	}

	public void decWidth() {
		this.setWidth(getWidth() - 2);
	}

	public void decHeight() {
		this.setHeight(getHeight() - 2);
	}

}

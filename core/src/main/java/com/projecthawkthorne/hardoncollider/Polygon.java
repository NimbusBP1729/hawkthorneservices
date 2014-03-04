package com.projecthawkthorne.hardoncollider;

/**
 * an arbitrary polygonal bound
 * 
 * @author Patrick
 * 
 */
class Polygon extends Bound {
	protected com.badlogic.gdx.math.Polygon p;

	/**
	 * creates a new polygon with the arrays given
	 * 
	 * @param x
	 *            an array of the x coordinates of the polygon
	 * @param y
	 *            an array of the y coordinates of the polygon
	 */
	Polygon(float[] vertices) {
		if (vertices  == null) {
			throw new NullPointerException(
					"Polygon requires non-null x and y coordinates");
		} else if (vertices.length%2 != 0) {
			throw new IllegalArgumentException(
					"Polygon requires even amount of vertices. Found " + vertices.length);
		} else if (vertices.length < 6) {
			throw new IllegalArgumentException(
					"Polygon requires at least 3 vertices values. Found " + vertices.length);
		}
		p = new com.badlogic.gdx.math.Polygon();
		p.setVertices(vertices);

	}

	/**
	 * returns true if the polygons have intersected
	 * 
	 * @param a
	 *            a polygon
	 * @param b
	 *            another polygon
	 * @return true if the polygons are likely to have intersected
	 */
	public static boolean intersects(Polygon a, Polygon b) {
		for (int i = 0; i < a.getVertices().length; i+=2) {
			if (b.p.contains(a.getVertices()[i], a.getVertices()[i+1])) {
				return true;
			}
		}
		for (int i = 0; i < b.getVertices().length; i+=2) {
			if (a.p.contains(b.getVertices()[i], b.getVertices()[i+1])) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean intersects(Polygon b, Rectangle r2) {
		if (b.p.contains(r2.x, r2.y)
				|| b.p.contains(r2.x + r2.width, r2.y)
				|| b.p.contains(r2.x, r2.y + r2.height)
				|| b.p.contains(r2.x + r2.width, r2.y + r2.height)) {
			return true;
		}
		for (int i = 0; i < b.getVertices().length; i+=2) {
			if (isBetween(b.getVertices()[i], r2.x, r2.x+r2.width) &&
				isBetween(b.getVertices()[i+1], r2.y, r2.y+r2.height)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isBetween(float testX, float bound1, float bound2) {
		if(testX > bound1 && testX > bound2) {
			return false;
		} else if(testX < bound1 && testX < bound2) {
			return false;
		} else {
			return true;
		}
	}

	// note: should consider calculating these in the constructor if the box is
	// never changed
	@Override
	public void bbox(float[] corners) {
		if (corners == null) {
			throw new UnsupportedOperationException("input array is null");
		} else if (corners.length != 4) {
			throw new UnsupportedOperationException(
					"incorrect array size: 4 required");
		}

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		float x, y;
		for (int i = 0; i < p.getVertices().length; i+=2) {
			x = p.getVertices()[i];
			y = p.getVertices()[i+1];
			minX = x < minX ? x : minX;
			minY = y < minY ? y : minY;
			maxX = x > maxX ? x : maxX;
			maxY = y > maxY ? y : maxY;
		}

		corners[0] = minX;
		corners[1] = minY;
		corners[2] = maxX;
		corners[3] = maxY;
	}

	@Override
	public void setX(float newX) {
		float[] corners = new float[4];
		this.bbox(corners);
		float minX = corners[0];
		float offsetX = newX - minX;
		for (int i = 0; i < p.getVertices().length; i+=2) {
			this.p.getVertices()[i] += offsetX;
		}
	}

	@Override
	public void setY(float newY) {
		float[] corners = new float[4];
		this.bbox(corners);
		float minY = corners[1];
		float offsetY = newY - minY;
		for (int i = 0; i < p.getVertices().length; i+=2) {
			this.p.getVertices()[i+1] += offsetY;
		}
	}

	@Override
	public float getSmallestY(float xVal) {
		float x1, x2, y1, y2;
		float minY = Float.MAX_VALUE;
		float curY;
		int n = this.p.getVertices().length;

		for (int i = 0; i < n; i+=2) {
			x1 = this.p.getVertices()[(i) % n];
			x2 = this.p.getVertices()[(i + 2) % n];
			y1 = this.p.getVertices()[(i + 1) % n];
			y2 = this.p.getVertices()[(i + 3) % n];
			if ((xVal < x1 && xVal > x2) || (xVal > x1 && xVal < x2)) {
				if (x1 == x2) {
					curY = y1;
				} else {
					curY = (y2 - y1) / (x2 - x1) * (xVal - x1) + y1;
				}
				minY = curY < minY ? curY : minY;
			}
		}
		return minY;
	}

	@Override
	public float getLargestY(float xVal) {
		float x1, x2, y1, y2;
		float maxY = -Float.MAX_VALUE;
		float curY;
		int n = this.p.getVertices().length;

		for (int i = 0; i < n; i+=2) {
			x1 = this.p.getVertices()[(i) % n];
			x2 = this.p.getVertices()[(i + 2) % n];
			y1 = this.p.getVertices()[(i + 1) % n];
			y2 = this.p.getVertices()[(i + 3) % n];
			if ((xVal < x1 && xVal > x2) || (xVal > x1 && xVal < x2)) {
				if (x1 == x2) {
					curY = y1;
				} else {
					curY = (y2 - y1) / (x2 - x1) * (xVal - x1) + y1;
				}
				maxY = curY > maxY ? curY : maxY;
			}
		}
		return maxY;
	}
	
	

	@Override
	public void draw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWidth(float newWidth) {
		float[] corners = new float[4];
		this.bbox(corners);
		float oldWidth = corners[2] - corners[0];
		int n = this.p.getVertices().length;
		for (int i = 0; i < n; i+=2) {
			this.p.getVertices()[i] = Math.round(this.p.getVertices()[i] * newWidth
					/ oldWidth);
		}
	}

	@Override
	public void setHeight(float newHeight) {
		float[] corners = new float[4];
		this.bbox(corners);
		float oldHeight = corners[2] - corners[0];
		int n = this.p.getVertices().length;
		for (int i = 0; i < n; i++) {
			this.p.getVertices()[i+1] = Math.round(this.p.getVertices()[i+1] * newHeight
					/ oldHeight);
		}
	}

	@Override
	public float getWidth() {
		float[] corners = new float[4];
		return corners[2] - corners[0];
	}

	@Override
	public float getHeight() {
		float[] corners = new float[4];
		return corners[3] - corners[1];
	}

	public float[] getVertices() {
		return p.getVertices();
	}
}

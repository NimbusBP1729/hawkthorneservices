package com.projecthawkthorne.hardoncollider;

/**
 * an arbitrary polygonal bound
 * 
 * @author Patrick
 * 
 */
class Polygon extends Bound {
	java.awt.Polygon p;

	/**
	 * creates a new polygon with the arrays given
	 * 
	 * @param x
	 *            an array of the x coordinates of the polygon
	 * @param y
	 *            an array of the y coordinates of the polygon
	 */
	Polygon(int[] x, int[] y) {
		if (x == null || y == null) {
			throw new NullPointerException(
					"Polygon requires non-null x and y coordinates");
		} else if (x.length < 3) {
			throw new IllegalArgumentException(
					"Polygon requires at least 3 x values. Found " + x.length);
		} else if (y.length < 3) {
			throw new IllegalArgumentException(
					"Polygon requires at least 3 y values. Found " + y.length);
		} else if (x.length != y.length) {
			throw new IllegalArgumentException(
					"Polygon requires the same amount of x and y values. Found "
							+ x.length + "," + y.length);

		}
		p = new java.awt.Polygon();
		p.xpoints = x;
		p.ypoints = y;
		p.npoints = p.xpoints.length;

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
		for (int i = 0; i < a.p.npoints; i++) {
			if (b.p.contains(a.p.xpoints[i], a.p.ypoints[i])) {
				return true;
			}
		}
		for (int i = 0; i < b.p.npoints; i++) {
			if (a.p.contains(b.p.xpoints[i], b.p.ypoints[i])) {
				return true;
			}
		}

		return false;
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
		for (int i = 0; i < p.npoints; i++) {
			x = p.xpoints[i];
			y = p.ypoints[i];
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
		for (int i = 0; i < this.p.npoints; i++) {
			this.p.xpoints[i] += offsetX;
		}
	}

	@Override
	public void setY(float newY) {
		float[] corners = new float[4];
		this.bbox(corners);
		float minY = corners[1];
		float offsetY = newY - minY;
		for (int i = 0; i < this.p.npoints; i++) {
			this.p.ypoints[i] += offsetY;
		}
	}

	@Override
	public float getSmallestY(float xVal) {
		float x1, x2, y1, y2;
		float minY = Float.MAX_VALUE;
		float curY;
		int n = this.p.npoints;

		for (int i = 0; i < n; i++) {
			x1 = this.p.xpoints[(i) % n];
			x2 = this.p.xpoints[(i + 1) % n];
			y1 = this.p.ypoints[(i) % n];
			y2 = this.p.ypoints[(i + 1) % n];
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
	public void draw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWidth(float newWidth) {
		float[] corners = new float[4];
		this.bbox(corners);
		float oldWidth = corners[2] - corners[0];
		int n = this.p.npoints;
		for (int i = 0; i < n; i++) {
			this.p.xpoints[i] = Math.round(this.p.xpoints[i] * newWidth
					/ oldWidth);
		}
	}

	@Override
	public void setHeight(float newHeight) {
		float[] corners = new float[4];
		this.bbox(corners);
		float oldHeight = corners[2] - corners[0];
		int n = this.p.npoints;
		for (int i = 0; i < n; i++) {
			this.p.xpoints[i] = Math.round(this.p.xpoints[i] * newHeight
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
}

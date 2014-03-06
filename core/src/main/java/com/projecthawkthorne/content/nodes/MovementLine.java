package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;

public class MovementLine {

	private Polyline line;
	public String name;

	public MovementLine(PolylineMapObject t) {
		this.line = t.getPolyline();
		this.name = t.getName();
	}

	public Polyline getLine() {
		return line;
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.Levels;
import com.projecthawkthorne.server.Keys;
import com.projecthawkthorne.server.Player;

/**
 * 
 * @author Patrick
 */
public class Door extends Node {
	/** true if this door causes automatic transport */
	private boolean instant = false;

	public Door(RectangleMapObject t, Level level) {
		super(t, level);
		level.getCollider().setPassive(this.bb);
		try {
			instant = (Boolean)(t.getProperties().get("instant"));
		} catch (Exception e) {
			instant = false;
		}
	}

	@Override
	public void collide(Node node) {
		if (!(node instanceof Player)) {
			return;
		}
		Player player = (Player) node;
		if (player.isTransporting) {
			return;
		}
		player.isTransporting = true;
		Door door = level.getDoor(this.properties.get("to",String.class));
		if (this.instant) {
			Levels.switchState(level, door, player, true);
		} else if (player.getKeyDown(Keys.INTERACT)) {
			Levels.switchState(level, door, player, true);
		}
		player.isTransporting = false;

	}

	@Override
	protected void updateVelocity(long dt) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void collideEnd(Node node) {
		// TODO Auto-generated method stub

	}

}

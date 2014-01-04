/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.Levels;

/**
 * 
 * @author Patrick
 */
public class Door extends Node {
	/** true if this door causes automatic transport */
	private boolean instant = false;
	private String destLevelName;

	public Door(RectangleMapObject t, Level level) {
		super(t, level);
		level.getCollider().setPassive(this.bb);
		instant = Boolean.parseBoolean(t.getProperties().get("instant",
				String.class));
		destLevelName = t.getProperties().get("level", String.class);
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
		if (this.instant || player.getIsKeyDown(GameKeys.INTERACT)) {
			Level destLevel = Levels.getSingleton().get(destLevelName);
			Door door = destLevel.getDoor(this.properties.get("to",
					String.class));
			Levels.switchState(destLevel, door, player);
		}
		player.isTransporting = false;

	}

	@Override
	protected void updateVelocity(long dt) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void collideEnd(Node node) {
		// TODO Auto-generated method stub

	}

}

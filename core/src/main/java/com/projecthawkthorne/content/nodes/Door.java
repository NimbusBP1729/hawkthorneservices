/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;

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
		if (this.instant) {
			Level destLevel = Level.get(destLevelName);
			Door door = destLevel.getDoor(this.properties.get("to",
					String.class));
			Level.switchState(destLevel, door, player);
		}
		player.isTransporting = false;

	}
	
	public boolean playerKeypressed(GameKeys button, Player player) {
		if(button == GameKeys.INTERACT && destLevelName!=null){
			Level destLevel = Level.get(destLevelName);
			Door door = destLevel.getDoor(this.properties.get("to",
					String.class));
			Level.switchState(destLevel, door, player);
			return true;
		}else{
			return false;
		}
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

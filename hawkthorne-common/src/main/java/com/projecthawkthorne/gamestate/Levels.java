/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.gamestate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.hardoncollider.Bound;

/**
 * 
 * @author Patrick
 */
public class Levels {
	private static final long serialVersionUID = 1L;
	private Map<String, Gamestate> levelMap;
	private static Levels singleton;

	private Levels() {
		// use this constructor to initialize all
		// gamestates that aren't levels
		levelMap = new HashMap<String, Gamestate>();
		levelMap.put("introduction", new Introduction());
		levelMap.put("overworld", new Overworld());
	}

	public static Levels getSingleton() {
		if (singleton == null) {
			singleton = new Levels();
		}
		return singleton;
	}

	private void put(String levelName, Gamestate level) {
		this.levelMap.put(levelName, level);
	}

	/**
	 * if door is null, the player ends up in his current location
	 * 
	 * @param newLevel
	 *            the destination level
	 * @param door
	 *            the door in the destination level
	 * @param player
	 *            the player being transported
	 * @param doReply
	 *            send a response back to the client(s) about this switch
	 */
	public static void switchState(Gamestate newLevel, Door door,
			Player player, boolean doReply) {
		Gamestate oldLevel = player.getLevel();
		oldLevel.removePlayer(player);
		player.setLevel(newLevel);
		player.stopJumping();
		player.getJumpQueue().flush();
		player.getCharacter().reset();
		player.velocityY = player.velocityX = 0;
		if (door != null) {
			player.x = door.x + door.width / 2 - player.width / 2;
			player.y = door.y + door.height - player.height;
		}
		newLevel.addPlayer(player);
		if (player.getCharacter().hasChanged()) {
			// TODO: hasChanged(true) should be done by the client
			// send a message to other clients here
			player.getCharacter().setChanged(false);
		}

		// if (this.attack_box!=null and this.attack_box ){;
		// this.collider:remove(this.attack_box.bb);
		// this.attack_box.bb = null;
		// };
		// this.collider = collider;

		Bound bb = player.getBb();
		if (oldLevel instanceof Level) {
			if (bb != null) {
				((Level) oldLevel).getCollider().removeBox(bb);
			}
		}

		if (newLevel instanceof Level) {
			if (bb == null) {
				player.setBb(Bound.create(player.x, player.y, 18, 44));
				bb = player.getBb();
			}
			((Level) newLevel).getCollider().addBox(bb);
			// this.moveBoundingBox();
			// this.attack_box = PlayerAttack.new(collider,self);;
		}

	}

	/**
	 * overloaded version of getValue that loads the Gamestate if necessary
	 * 
	 * @param key
	 * @return
	 */
	// public Gamestate getValue(String key){
	// Gamestate level;
	// super.g
	// level = super.getValue((Object)key);
	// if( level == null ){
	// level = new Gamestate(key);
	// this.put(key, level);
	// }
	// return level;
	// }
	/**
	 * overloaded version of getValue that loads the Gamestate if necessary
	 * 
	 * @param key
	 *            the level name
	 * @return the level
	 */
	public Gamestate get(String levelName) {
		levelName = levelName.trim();
		if (levelName == null || levelName.isEmpty()) {
			return null;
		}
		Gamestate level = this.levelMap.get(levelName);
		if (level == null) {
			level = new Level(levelName);
			this.put(levelName, level);
		}
		return level;
	}

	public Collection<Gamestate> values() {
		return levelMap.values();
	}

}

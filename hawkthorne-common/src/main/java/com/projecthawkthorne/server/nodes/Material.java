/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server.nodes;

import java.util.Iterator;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.server.Keys;
import com.projecthawkthorne.server.Player;

/**
 * 
 * @author Patrick
 */
public class Material extends Item {
	public Material(RectangleMapObject obj, Level level) {
		super(obj, level);
		level.getCollider().setPassive(this.bb);
		// this.objectTexture// = new
		// Texture(Gdx.files.internal(IMAGES_FOLDER+"materials/"+this.name+".png"));
		this.width = 24;
		this.height = 24;
	}

	@Override
	public void setKeyDown(Keys button, boolean b) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void collide(Node n) {
	}

	@Override
	protected void updateVelocity(long dt) {
		Iterator<Player> it = this.playersTouched.iterator();
		while (it.hasNext()) {
			Player player = it.next();
			if (player.getKeyDown(Keys.UP)) {
				player.getInventory().addItem(this);
				this.die();
			}
		}
	}

	@Override
	protected void collideEnd(Node node) {
	}

	@Override
	public void use() {
		// materials don't do anything
	}

}

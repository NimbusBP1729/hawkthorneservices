/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import java.util.Iterator;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;

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
	public void setIsKeyDown(GameKeys button, boolean b) {
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
			if (player.getIsKeyDown(GameKeys.UP)) {
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;


public class Projectile extends Weapon {

    public Projectile(RectangleMapObject obj, Level level) {
        super(obj, level);
    }

    @Override
    public void collide(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateVelocity(long dt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public void collideEnd(Node node) {
		// TODO Auto-generated method stub
		
	}

}

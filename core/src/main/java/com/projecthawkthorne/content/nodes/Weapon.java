/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;

/**
 *
 * @author Patrick
 */
public abstract class Weapon extends Item {

    public Weapon(RectangleMapObject obj, Level level) {
        super(obj, level);
    }
    
    /**
     * dequips a weapon
     */
    public void unuse() {}

    /**
     * takes out or uses a weapon
     * @param user
     */
    public void use(Player user) {
        
    }

    /**
     * drops a weapon
     */
    public void drop() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void collide(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void collideEnd(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void use() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateVelocity(long dt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}

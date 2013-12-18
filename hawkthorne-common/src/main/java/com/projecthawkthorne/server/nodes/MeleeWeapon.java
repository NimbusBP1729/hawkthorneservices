/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;


public class MeleeWeapon extends Weapon {
    protected boolean wielding;

    public MeleeWeapon(RectangleMapObject obj, Level level) {
        super(obj, level);
    }

    public void wield() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void setWielding(boolean b) {
        this.wielding = b;
    }
    
}

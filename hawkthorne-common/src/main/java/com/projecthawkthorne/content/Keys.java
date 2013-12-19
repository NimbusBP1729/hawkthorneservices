/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

/**
 *
 * @author Patrick
 */
public enum Keys {
    LEFT,RIGHT,UP,DOWN,JUMP,ATTACK,INTERACT,START,SELECT;
    public static Keys parse(String name){
        return Keys.valueOf(name.trim());
    }
}

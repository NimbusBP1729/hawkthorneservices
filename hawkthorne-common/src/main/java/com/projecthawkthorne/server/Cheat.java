/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server;

/**
 *
 * @author Patrick
 */
public class Cheat {
    private boolean jump_high = false;
    private boolean god = false;
	public boolean isJumpHigh() {
		return jump_high;
	}
	public void jumpHigh() {
		jump_high = true;
	}
	public boolean isGod() {
		return god;
	}
	public void setGod(boolean god) {
		this.god = god;
	}
    
}

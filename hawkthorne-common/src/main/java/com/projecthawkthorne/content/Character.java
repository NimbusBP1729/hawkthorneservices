/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

import com.projecthawkthorne.content.nodes.State;

/**
 * 
 * @author Patrick
 */
public class Character {
	boolean changed = false;
	Direction direction = Direction.RIGHT;
	private State state = State.IDLE;
	private LuaAnimation animation = new LuaAnimation();
	private String name;
	private String costume;

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean newValue) {
		changed = newValue;
	}

	void setState(State state) {
		this.state = state;
	}

	LuaAnimation getAnimation() {
		return animation;
	}

	State getState() {
		return state;
	}

	public void reset() {
		this.setState(State.IDLE);
	}

	/**
	 * @return the character's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param character
	 *            the character to set
	 */
	public void setName(String character) {
		this.name = character;
	}

	/**
	 * @return the costume
	 */
	public String getCostume() {
		return costume;
	}

	/**
	 * @param costume
	 *            the costume to set
	 */
	public void setCostume(String costume) {
		this.costume = costume.trim();
	}

}

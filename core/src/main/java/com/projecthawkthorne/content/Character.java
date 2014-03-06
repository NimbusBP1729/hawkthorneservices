/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.nodes.State;

/**
 * 
 * @author Patrick
 */
public class Character {
	private State state = State.IDLE;
	private LuaAnimation animation = new LuaAnimation();
	private String name;
	private String costume;

	public Character() {
		name = "garrett";
		costume = "base";
	}

	void setState(State state) {
		if(this.state!=State.WALK && state == State.WALK){
			Assets.playSfx("footsteps3",true);
		}else if(this.state==State.WALK && state != State.WALK){
			Assets.stopSfx("footsteps3");
		}
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

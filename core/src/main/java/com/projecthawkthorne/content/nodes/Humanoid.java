package com.projecthawkthorne.content.nodes;

import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.FloorCollidable;
import com.projecthawkthorne.content.Footprint;
import com.projecthawkthorne.content.PlayerState;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Collidable;

public abstract class Humanoid extends Node implements FloorCollidable {

	protected PlayerState current_state_set;
	protected PlayerState previous_state_set;
	protected State crouch_state;
	protected State walk_state;
	protected State gaze_state;
	protected State jump_state;
	protected State idle_state;
	private Footprint footprint;

	private Platform dropPlatform;
	private Climbable climbable = null;
	private boolean isClimbing = false;

	public Humanoid(RectangleMapObject obj, Level level) {
		super(obj, level);
	}

	public Humanoid(RectangleMapObject obj, Level level, UUID id) {
		super(obj, level, id);
	}

	/**
	 * returns true if the humanoid is dropping from the platform
	 * 
	 * @param platform
	 * @return true if this is dropping
	 */
	public final boolean isDroppingFrom(Platform platform) {
		return (platform == dropPlatform);
	}

	/**
	 * the platform you want to drop from
	 * 
	 * @param platform
	 */
	public final void dropFromPlatform(Platform platform) {
		dropPlatform = platform;
	}

	protected boolean isClimbing() {
		return isClimbing;
	}

	protected void unClimb(Climbable c) {
		assert (this.climbable != null);
		assert (this.isClimbing);
		this.dropFromPlatform(null);

		// FIXME: use previous_state_set
		this.setSpriteStates(PlayerState.DEFAULT);
		this.isClimbing = false;
	}

	// FIXME:isClimbing should only be true when you have a climbable
	/**
	 * climbs a climbable (e.g. a ladder)
	 * 
	 * @param c
	 *            the climbable
	 */
	protected void climb(Climbable c) {
		if (isClimbing) {
			assert (this.climbable != null);
		}
		Iterator<Collidable> it = this.getCollisionList().iterator();
		while (it.hasNext()) {
			Collidable p = it.next();
			if (p instanceof Platform) {
				this.dropFromPlatform((Platform) p);
			}
		}

		this.setSpriteStates(PlayerState.CLIMBING);
		this.isClimbing = true;
	}

	/**
	 * returns the climbable you're touching
	 * 
	 * @return the climbable
	 */
	public Climbable getClimbable() {
		return climbable;
	}

	/**
	 * sets the climbable
	 * 
	 * @param climbable
	 *            the ladder
	 */
	public void setClimbable(Climbable climbable) {
		if (climbable == null) {
			assert (!this.isClimbing());
		}
		this.climbable = climbable;
	}

	/**
	 * Sets the sprite states of a player based on a preset combination <br>
	 * call this public void if an action requires a set of state changes
	 * 
	 * @param presetName
	 *            the state set to use
	 */
	public final void setSpriteStates(PlayerState presetName) {
		// walk_state : pressing left or right
		// crouch_state: pressing down
		// gaze_state : pressing up
		// jump_state : pressing jump button
		// idle_state : standing around

		this.previous_state_set = this.current_state_set == null ? PlayerState.DEFAULT
				: this.current_state_set;
		this.current_state_set = presetName;
		if (presetName == PlayerState.WIELDING) {
			this.walk_state = State.WIELDWALK;
			if (this.footprint != null) {
				this.crouch_state = State.CROUCHWALK;
				this.gaze_state = State.GAZEWALK;
			} else {
				this.crouch_state = State.CROUCH;
				this.gaze_state = State.IDLE;
			}
			this.jump_state = State.WIELDJUMP;
			this.idle_state = State.WIELDIDLE;
		} else if (presetName == PlayerState.HOLDING) {
			this.walk_state = State.HOLDWALK;
			if (this.footprint != null) {
				this.crouch_state = State.HOLDWALK;
				this.gaze_state = State.HOLDWALK;
			} else {
				this.crouch_state = State.CROUCH;
				this.gaze_state = State.IDLE;
			}
			this.jump_state = State.HOLDJUMP;
			this.idle_state = State.HOLD;
		} else if (presetName == PlayerState.ATTACKING) { // state for sustained
			// attack
			this.walk_state = State.ATTACKWALK;
			this.crouch_state = State.ATTACK;
			this.gaze_state = State.ATTACK;
			this.jump_state = State.ATTACKJUMP;
			this.idle_state = State.ATTACK;
		} else if (presetName == PlayerState.CLIMBING) { // state for sustained
															// attack
			this.walk_state = State.GAZEWALK;
			this.crouch_state = State.GAZEWALK;
			this.gaze_state = State.GAZEWALK;
			this.jump_state = State.GAZEWALK;
			this.idle_state = State.GAZEIDLE;
		} else if (presetName == PlayerState.LOOKING) {
			this.walk_state = State.WALK;
			if (this.footprint != null) {
				this.crouch_state = State.CROUCHWALK;
				this.gaze_state = State.GAZEWALK;
			} else {
				this.crouch_state = State.CROUCH;
				this.gaze_state = State.GAZE;
			}
			this.jump_state = State.JUMP;
			this.idle_state = State.IDLE;
		} else if (presetName == PlayerState.DEFAULT) {
			// Default
			this.walk_state = State.WALK;
			if (this.footprint != null) {
				this.crouch_state = State.CROUCHWALK;
				this.gaze_state = State.GAZEWALK;
			} else {
				this.crouch_state = State.CROUCH;
				this.gaze_state = State.IDLE;
			}
			this.jump_state = State.JUMP;
			this.idle_state = State.IDLE;
		} else {
			System.err
					.println("Error! invalid spriteState " + presetName + ".");
		}
	}

	// TODO:assert that floor collisions reset the dropPlatform

}

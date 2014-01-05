/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.nodes.Climbable;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Enemy;
import com.projecthawkthorne.content.nodes.Humanoid;
import com.projecthawkthorne.content.nodes.MeleeWeapon;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.content.nodes.State;
import com.projecthawkthorne.content.nodes.Weapon;
import com.projecthawkthorne.datastructures.Queue;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;
import com.projecthawkthorne.hardoncollider.Collidable;
import com.projecthawkthorne.socket.Client;
import com.projecthawkthorne.socket.Command;
import com.projecthawkthorne.socket.MessageBundle;
import com.projecthawkthorne.timer.Timeable;
import com.projecthawkthorne.timer.Timer;

/**
 * 
 * @author Patrick
 */
public class Player extends Humanoid implements Timeable {

	private static Map<UUID, Player> playerMap = new HashMap<UUID, Player>();
	private Character character = new Character();
	private EnumMap<GameKeys, Boolean> keyDown = new EnumMap<GameKeys, Boolean>(
			GameKeys.class);
	public static int startingMoney = 0;
	private boolean invulnerable = false;
	private static final int max_health = 6;
	private static int playerCount = 0;
	private int health = Player.max_health;
	private Inventory inventory;
	private int money = Player.startingMoney;
	private int lives = Integer.MAX_VALUE;
	private Queue<String> jumpQueue = new Queue<String>();
	private Queue<String> halfjumpQueue = new Queue<String>();
	private boolean rebounding = false;
	private int damageTaken = 0;
	private boolean jumping = false;
	private boolean liquidDrag = false;
	private boolean flash = false;
	private int fall_damage = 0;
	private long since_solid_ground = 0;
	private Node currently_held; // Object currently being held by the player
	private Node holdable; // Object that would be picked up if player used grab
							// key
	private boolean interactive_collide;
	private PlayerAttack attack_box;
	private Footprint footprint;
	private HealthText healthText = new HealthText();
	private HealthVelocity healthVel = new HealthVelocity();
	private boolean hurt;
	private boolean wielding = false;
	private boolean prevAttackPressed;
	private Object blink;
	public static final float jumpFactor = 0.0010f;
	private float halfJumpStrength = 450 * jumpFactor;
	private float highJumpStrength = 970 * jumpFactor;
	private float normalJumpStrength = 670 * jumpFactor;
	private float liquidJumpStrength = 270 * jumpFactor;
	public boolean isTransporting = false;
	public Enemy currentEnemy;
	public long down_dt = 0;
	private Cheat cheat = new Cheat();
	public int jumpDamage = 1;
	private String username;
	/** the list of nodes this player needs fresh information about */
	public Set<Node> updateList = new HashSet<Node>();
	private static Player singleton;

	private Player(RectangleMapObject obj, Level level, UUID id) {
		super(obj, level, id);
		this.username = "<" + id.toString().substring(0, 4) + "...>";
		this.bboxOffsetX = 15;
		this.bboxOffsetY = 0;

		this.setSpriteStates(PlayerState.DEFAULT);
		for (GameKeys button : GameKeys.values()) {
			keyDown.put(button, false);
		}

		this.inventory = new Inventory(this);
	}

	private Player(RectangleMapObject obj, UUID id) {
		this(obj, null, id);
	}

	private static RectangleMapObject getPlayerTiledObject() {
		RectangleMapObject obj = new RectangleMapObject();
		obj.getRectangle().width = Math.round(48);
		obj.getRectangle().height = Math.round(48);
		obj.getRectangle().x = 0;
		obj.getRectangle().y = obj.getRectangle().height;
		obj.getProperties().put("type", "player");
		obj.setName(Integer.toString(Player.playerCount++));
		obj.getProperties().put("bbox_width", Integer.toString(Math.round(18)));
		obj.getProperties()
				.put("bbox_height", Integer.toString(Math.round(44)));
		return obj;
	}

	public Character getCharacter() {
		return character;
	}

	// -
	// Drops an object.
	// @return null
	public void drop() {

		if (this.currently_held != null
				&& this.currently_held instanceof Weapon) {
			Weapon weapon = (Weapon) this.currently_held;
			weapon.drop();
		} else if (this.currently_held != null) {
			this.setSpriteStates(PlayerState.DEFAULT);
			Node object_dropped = this.currently_held;
			this.currently_held = null;
			// (object_dropped.drop) {
			object_dropped.drop(this);
			// }
		}
	}

	// Picks up an object.
	// @return null
	public void pickup() {
		this.setSpriteStates(PlayerState.HOLDING);
		this.currently_held = this.holdable;
		if (this.currently_held != null) {
			this.currently_held.pickup(this);
		}
	}

	// Throws an object.
	// @return null
	public void throw_normal() {
		if (this.currently_held != null
				&& this.currently_held instanceof Weapon) { // weapon does
															// nothing
		} else if (this.currently_held != null) {
			this.setSpriteStates(PlayerState.DEFAULT);
			Node object_thrown = this.currently_held;
			this.currently_held = null;
			// if (object_thrown.throw ){
			object_thrown.throw_normal(this); // }
		}
	}

	// -
	// Throws an object vertically.
	// @return null
	public void throw_vertical() {

		if (this.currently_held != null
				&& this.currently_held instanceof Weapon) {
			// throw_vertical action
		} else if (this.currently_held != null) {
			this.setSpriteStates(PlayerState.DEFAULT);
			Node object_thrown = this.currently_held;
			this.currently_held = null;
			// if (object_thrown.throw_vertical ){
			object_thrown.throw_vertical(this);
			// }
		}
	}

	// -
	// Get whether the player has the ability to jump from here
	// @return bool
	public boolean solid_ground() {

		if (this.since_solid_ground < Game.fall_grace || this.isClimbing()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setIsKeyDown(GameKeys button, boolean b) {
		this.keyDown.put(button, Boolean.valueOf(b));
	}

	public boolean getIsKeyDown(GameKeys button) {
		return keyDown.get(button);
	}

	/**
	 * @param character
	 *            the character to set
	 */
	public void setCharacter(Character character) {
		this.character = character;
	}

	// Switches weapons. if there"s nothing to switch to
	// this switches to default attack
	// @return null
	public void switchWeapon() {

		Weapon newWeapon = this.inventory.getCurrentWeapon();
		Weapon oldWeapon;
		oldWeapon = (Weapon) (this.currently_held);
		oldWeapon.unuse();

		if (newWeapon != null) {
			newWeapon.use(this);
			this.setSpriteStates(PlayerState.WIELDING);
		}
	}

	/**
	 * called when a player presses a key
	 * 
	 * @param button
	 *            the button that was pressed
	 * @return
	 */
	public boolean keypressed(GameKeys button) {
		if (this.dead) {
			return false;
		}

		// if (this.inventory.isVisible()) {
		// this.inventory.keypressed(button);
		// return;
		// }

		if (button == GameKeys.SELECT && !this.interactive_collide) {
			if ((this.currently_held != null)
					&& (this.currently_held instanceof Weapon)
					&& this.keyDown.get(GameKeys.DOWN)) {
				Weapon weapon = (Weapon) this.currently_held;
				weapon.unuse();
				return true;
			} else if ((this.currently_held != null)
					&& (this.currently_held instanceof Weapon)
					&& this.keyDown.get(GameKeys.UP)) {
				this.switchWeapon();
				return true;
			} else {
				this.inventory.open();
				return true;
			}
		}

		if (button == GameKeys.INTERACT && !this.interactive_collide) {
			if ((this.currently_held != null)
					&& !(this.currently_held instanceof Weapon)) {
				if (this.keyDown.get(GameKeys.DOWN)) {
					this.drop();
					return true;
				} else if (this.keyDown.get(GameKeys.UP)) {
					this.throw_vertical();
					return true;
				} else {
					this.throw_normal();
					return true;
				}
			} else if ((this.holdable != null) && (this.currently_held != null)) {
				this.pickup();
				return true;
			} else {
				this.attack();
				return true;
			}
		}

		Iterator<Collidable> it = this.collisionList.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			if (node.playerKeypressed(button, this)) {
				return true;
			}
		}

		if ((button == GameKeys.UP || button == GameKeys.DOWN)) {
			if (this.getClimbable() != null) {
				this.climb(this.getClimbable());
				return true;
			}
		}
		// taken from sonic physics http://info.sonicretro.org/SPG:Jumping
		if (button == GameKeys.JUMP) {
			this.getJumpQueue().push("jump");
			return true;
		}
		return false;
	}

	public void keyreleased(GameKeys button) { // taken from sonic physics
												// http://info.sonicretro.org/SPG:Jumping

		if (button == GameKeys.JUMP) {
			this.halfjumpQueue.push("jump");
		}
	}

	@Override
	public void die() {
		this.die(this.health);
	}

	public void die(int damage) {

		if (this.isInvulnerable() || this.getCheat().isGod() || this.dead) {
			return;
		}

		// damage = Math.floor(damage)
		if (damage == 0) {
			return;
		}

		// local msg = string.format("%s %s %s",this.id,"sound", "damage_"..
		// math.max(this.health, 0) )
		// server:s}toplayer(msg,"*")
		this.setRebounding(true);
		this.setInvulnerable(true);
		// ach:achieve("damage", damage)

		// if (damage != null ){
		this.healthText.x = this.x + this.width / 2;
		this.healthText.y = this.y;
		this.healthVel.y = -35;
		this.damageTaken = damage;
		this.health = Math.max(this.health - damage, 0);
		// }

		if (this.health <= 0) {
			// the true death
			this.dead = true;
			this.character.setState(State.DEAD);
			this.lives = this.lives - 1;
			// ach:achieve("die")
			// sound.stopMusic()
			// TODO: reimplement death sound
			// Messages.broadcast(msg); // start death sequence

			Timer.add(3000, "PLAYER_DIED", this);
		} else {
			this.hurt = true;
			this.character.setState(State.HURT);
		}

		Timer.add(400, "PLAYER_NOT_HURT", this);

		Timer.add(1500, "PLAYER_NOT_HURT2", this);

		this.startBlink();
	}

	// -
	// This is the main update loop for the player, handling position updates.
	// @param dt The time delta
	// @return null

	@Override
	protected void updateVelocity(long dt) {
		Level level = (Level) this.level;
		if (level == null) {
			return;
		}
		boolean DOWN_MOTION = this.keyDown.get(GameKeys.DOWN) && !isDead();
		boolean UP_MOTION = this.keyDown.get(GameKeys.UP) && !isDead();
		boolean LEFT_MOTION = this.keyDown.get(GameKeys.LEFT) && !isDead();
		boolean RIGHT_MOTION = this.keyDown.get(GameKeys.RIGHT) && !isDead();

		if (!this.isInvulnerable()) {
			this.stopBlink();
		}

		if (this.health <= 0) {
			if (this.currently_held != null
					&& this.currently_held instanceof Weapon) {
				Weapon weapon = (Weapon) (this.currently_held);
				weapon.unuse();
			}
		}

		if (DOWN_MOTION && UP_MOTION) {
			DOWN_MOTION = false;
			UP_MOTION = false;
		}
		if (LEFT_MOTION && RIGHT_MOTION) {
			LEFT_MOTION = false;
			RIGHT_MOTION = false;
		}

		// taken from sonic physics http://info.sonicretro.org/SPG:Running
		if (LEFT_MOTION && !RIGHT_MOTION && !this.isRebounding()) {

			if (DOWN_MOTION && State.CROUCH == this.crouch_state) {
				this.velocityX = this.velocityX + this.accel() * dt;
				if (this.velocityX > 0) {
					this.velocityX = 0;
				}
			} else if (this.velocityX > 0) {
				this.velocityX = this.velocityX - this.decel() * dt;
			} else if (this.velocityX > -Game.max_x) {
				this.velocityX = this.velocityX - this.accel() * dt;
				if (this.velocityX < -Game.max_x) {
					this.velocityX = -Game.max_x;
				}
			}

		} else if (RIGHT_MOTION && !LEFT_MOTION && !this.isRebounding()) {

			if (DOWN_MOTION && State.CROUCH == this.crouch_state) {
				this.velocityX = this.velocityX - this.accel() * dt;
				if (this.velocityX < 0) {
					this.velocityX = 0;
				}
			} else if (this.velocityX < 0) {
				this.velocityX = this.velocityX + this.decel() * dt;
			} else if (this.velocityX < Game.max_x) {
				this.velocityX = this.velocityX + this.accel() * dt;
				if (this.velocityX > Game.max_x) {
					this.velocityX = Game.max_x;
				}
			}
		} else {
			if (this.velocityX < 0) {
				this.velocityX = Math.min(this.velocityX + Game.friction * dt,
						0);
			} else {
				this.velocityX = Math.max(this.velocityX - Game.friction * dt,
						0);
			}
		}

		boolean jumped = getJumpQueue().flush();
		boolean halfjumped = halfjumpQueue.flush();

		if (jumped && !this.isJumping() && this.solid_ground()
				&& !this.isRebounding() && !this.isLiquid_drag()) {
			this.setJumping(true);
			if (this.getCheat().isJumpHigh()) {
				this.velocityY = this.highJumpStrength;
			} else {
				this.velocityY = this.normalJumpStrength;
			}
			if (this.isClimbing()) {
				this.unClimb(this.getClimbable());
			}

		} else if (jumped && !this.isJumping() && this.solid_ground()
				&& !this.isRebounding() && this.isLiquid_drag()) {
			// Jumping through heavy liquid:
			this.setJumping(true);
			this.velocityY = this.liquidJumpStrength;
			if (this.isClimbing()) {
				this.unClimb(this.getClimbable());
			}
		}
		if (halfjumped && !this.isRebounding() && this.isJumping()
				&& this.solid_ground()) {
			this.velocityY = this.halfJumpStrength;
		}

		// not sure discounting footprint is a transparent
		// way to write this
		// Note: this.footprint should be null in all 2D floorspaces
		// FIXME:isClimbing should only be true when you have a climbable
		if (this.isClimbing()) {
			// FIXME: positional code should not be in updateVelocity
			Climbable vine = this.getClimbable();
			if (DOWN_MOTION) {
				this.velocityY = -this.getClimbable().speed;
				this.x = vine.x + vine.width / 2 - this.width / 2;
			} else if (UP_MOTION) {
				this.velocityY = this.getClimbable().speed;
				this.x = vine.x + vine.width / 2 - this.width / 2;
			} else {
				this.velocityY = 0;
			}
		} else if (this.footprint == null || this.isJumping()) {
			this.velocityY = this.velocityY + Game.gravity * dt;
		}

		this.since_solid_ground = this.since_solid_ground + dt;
		if (this.velocityY > Game.maxVelocityY) {
			this.velocityY = Game.maxVelocityY;
			this.fall_damage = Math.round((this.fall_damage + Game.fall_dps
					* dt));
		}

		// falling off the bottom of the map
		if (this.y < 0) {
			this.die(this.health);
			return;
		}

		if (this.velocityX < 0) {
			this.direction = Direction.LEFT;
		} else if (this.velocityX > 0) {
			this.direction = Direction.RIGHT;
		}

		if (this.wielding || this.hurt || this.isDead()) {

		} else if (this.isJumping()) {

			this.character.setState(this.jump_state);
		} else if (this.isJumpState(this.character.getState())
				&& !this.isJumping()) {

			this.character.setState(this.walk_state);
		} else if (!this.isJumpState(this.character.getState())
				&& this.velocityX != 0) {

			if (DOWN_MOTION && State.CROUCH == this.crouch_state) {
				this.character.setState(this.crouch_state);
			} else {
				this.character.setState(this.walk_state);
			}

		} else if (!this.isJumpState(this.character.getState())
				&& this.velocityX == 0) {

			if (DOWN_MOTION && UP_MOTION) {
				this.character.setState(this.idle_state);
			} else if (DOWN_MOTION) {
				this.character.setState(this.crouch_state);
			} else if (UP_MOTION) {
				this.character.setState(this.gaze_state);
			} else {
				this.character.setState(this.idle_state);
			}

		}

		this.character.getAnimation().update(dt);

		this.healthText.y = this.healthText.y + this.healthVel.y * dt;
		if (this.x < -this.width / 4) {
			this.x = -this.width / 4;
		} else if (this.x > level.getBoundary().width - this.width * 3 / 4) {
			this.x = level.getBoundary().width - this.width * 3 / 4;
		}
	}

	public void revive() {

		this.health = Player.max_health;
		this.dead = false;
		this.money = 0;
		// this.inventory = new Inventory(this);
		this.stopBlink();
		this.character.reset();
		Gdx.app.log("player activity", "character revived");
	}

	// -
	// Call to take falling damage, and reset this.fall_damage to 0
	// @return null
	public void impactDamage() {

		if (this.fall_damage > 0) {
			this.die(this.fall_damage);
		}
		this.fall_damage = 0;
	}

	// -
	// Stops the player from blinking, clearing the damage queue, and correcting
	// the
	// flash animation
	// @return null
	public void stopBlink() {

		if (this.blink != null) {
			// Timer.cancel(this.blink);
			this.blink = null;
		}
		this.damageTaken = 0;
		this.flash = false;
	}

	// -
	// -
	// Starts the player blinking every .12 seconds if they are not already
	// blinking
	// @return null
	public void startBlink() {

		if (this.blink == null) {
			// this.blink = Timer.addPeriodic(.12, function()
			// this.flash = not this.flash
			// })
		}
	}

	public boolean isJumpState(String myState) { // assert(type(myState) ==
													// "string")

		if (myState == null) {
			return false;
		}

		if (myState.contains("jump")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isWalkState(String myState) {

		if (myState == null) {
			return false;
		}

		if (myState.contains("walk")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isIdleState(String myState) { // assert(type(myState) ==
													// "string")

		if (myState == null) {
			return false;
		}

		if (myState.contains("idle")) {
			return true;
		} else {
			return false;
		}
	}

	// //- Platformer interface
	public void ceiling_pushback(Node node, float new_y) {
		throw new UnsupportedOperationException(
				"need to implement ceiling pushbacks");
	}

	/**
	 * Function to call when colliding with the ground
	 */
	public void restore_solid_ground() {

		this.since_solid_ground = 0;
	}

	/**
	 * Registers an object as something that the user can currently hold on to
	 * 
	 * @param holdable
	 */
	public void registerHoldable(Node holdable) {
		if (this.holdable == null && this.currently_held == null) {
			this.holdable = holdable;
		}
	}

	/**
	 * Cancels the holdability of a node
	 * 
	 * @param holdable
	 */
	public void cancelHoldable(Node holdable) {

		if (this.holdable == holdable) {
			this.holdable = null;
		}
	}

	/**
	 * The player attacks
	 */
	public void attack() {

		Weapon currentWeapon = this.inventory.getCurrentWeapon();
		// take out a weapon
		if (this.prevAttackPressed) {
			return;
		}

		if (this.currently_held != null
				&& this.currently_held instanceof MeleeWeapon) {
			this.prevAttackPressed = true;
			MeleeWeapon weapon = (MeleeWeapon) this.currently_held;
			weapon.wield();

			Timer.add(1000, "BACK", this);
			// use a default attack
		} else if (this.currently_held != null) {
			// do nothing if we have a nonwieldable
		} else if (currentWeapon != null) {
			currentWeapon.use(this);
			if (this.currently_held != null
					&& this.currently_held instanceof MeleeWeapon) {
				this.setSpriteStates(PlayerState.WIELDING);
			}
			// punch/kick
		} else {
			// this.attack_box.activate();
			this.prevAttackPressed = true;
			this.setSpriteStates(PlayerState.ATTACKING);
			Timer.add(500, "ATTACK1", this);
			Timer.add(1100, "ATTACK2", this);
		}
	}

	/**
	 * returns true if the state is a jump state
	 * 
	 * @param state
	 * @return true if the state is a jump state
	 */
	private boolean isJumpState(State state) {
		switch (state) {
		case JUMP:
			return true;
		case WIELDJUMP:
			return true;
		case HOLDJUMP:
			return true;
		case ATTACKJUMP:
			return true;
		default:
			return false;
		}
	}

	@Override
	public State getState() {
		return this.character.getState();
	}

	@Override
	public void collide(Node node) {
		// node handles collisions
	}

	@Override
	public void floorPushback(Bound floor, float newY) {
		// this.ceiling_pushback(node, new_y);
		this.y = newY - this.bboxOffsetY;
		this.velocityY = 0;
		// this.moveBoundingBox();
		this.setJumping(false);
		this.setRebounding(false);

		this.impactDamage();
		this.restore_solid_ground();
	}

	@Override
	public void handleTimer(String name) {
		if (name.equals("ATTACK1")) {
			// plyr.attack_box
			// :deactivate();
			this.setSpriteStates(this.previous_state_set);
		} else if (name.equals("ATTACK2")) {
			this.prevAttackPressed = false;
		} else if (name.equals("BACK")) {
			this.wielding = false;
			if (this.currently_held instanceof MeleeWeapon
					&& this.currently_held != null) {
				MeleeWeapon mw = (MeleeWeapon) this.currently_held;
				mw.setWielding(false);
			}
			this.prevAttackPressed = false;
		} else if (name.equals("PLAYER_DIED")) {
			this.revive();
			if (this.lives <= 0) {
				// TODO: reimplement gameover
			} else {
				// TODO: remove casts
				Level spawnLevel = ((Level) level).getSpawnLevel();
				Door main = spawnLevel.getDoor("main");
				Level.switchState(spawnLevel, main, this);
			}

		} else if (name.equals("PLAYER_NOT_HURT")) {
			this.hurt = false;
		} else if (name.equals("PLAYER_NOT_HURT2")) {
			this.invulnerable = false;
			this.flash = false;
		} else {
			Gdx.app.error("Unknown timer in Player:", name.toString());
		}
	}

	/**
	 * returns the x-acceleration of this node when it starts running
	 * 
	 * @return the x-acceleration of this node when it starts running
	 */
	private float accel() {
		if (this.velocityY < 0) {
			return Game.airaccel;
		} else {
			return Game.accel;
		}
	}

	/**
	 * returns the x deceleration of this node when it stops running
	 * 
	 * @return the x deceleration of this node when it stops running
	 */
	private float decel() {
		if (this.velocityY < 0) {
			return Game.airaccel;
		} else {
			return Game.deccel;
		}
	}

	@Override
	/**
	 * Note: most nodes handle collisions for the player
	 */
	protected void collideEnd(Node node) {
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public boolean isRebounding() {
		return rebounding;
	}

	public boolean isDead() {
		return dead;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public Cheat getCheat() {
		return cheat;
	}

	public void setCheat(Cheat cheat) {
		this.cheat = cheat;
	}

	public void stopJumping() {
		this.setJumping(false);
	}

	/**
	 * @return the jumpQueue
	 */
	public Queue<String> getJumpQueue() {
		return jumpQueue;
	}

	/**
	 * @param jumpQueue
	 *            the jumpQueue to set
	 */
	public void setJumpQueue(Queue<String> jumpQueue) {
		this.jumpQueue = jumpQueue;
	}

	@Override
	public void wallPushback(Bound bb, float newX, boolean wallOnRight) {
		if (wallOnRight) {
			this.x = newX - this.bboxOffsetX - this.bbox_width;
		} else {
			this.x = newX - this.bboxOffsetX;
		}
		this.velocityX = 0;
		this.moveBoundingBox();
	}

	@Override
	public void ceilingPushback(Bound bb, float newY) {
		this.y = newY + this.bboxOffsetY;
		this.velocityY = 0;
		this.moveBoundingBox();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	/**
	 * @param rebounding
	 *            the rebounding to set
	 */
	public void setRebounding(boolean rebounding) {
		this.rebounding = rebounding;
	}

	/**
	 * @return the liquid_drag
	 */
	public boolean isLiquid_drag() {
		return liquidDrag;
	}

	/**
	 * @param liquidDrag
	 *            the liquid_drag to set
	 */
	public void setLiquidDrag(boolean liquidDrag) {
		this.liquidDrag = liquidDrag;
	}

	/**
	 * @return the jumping
	 */
	public boolean isJumping() {
		return jumping;
	}

	/**
	 * @param jumping
	 *            the jumping to set
	 */
	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public static Player getSingleton() {
		if (HawkthorneGame.MODE == Mode.SERVER) {
			throw new UnsupportedOperationException();
		}
		if (singleton == null) {
			singleton = new Player(Player.getPlayerTiledObject(),
					UUID.randomUUID());
			MessageBundle message = new MessageBundle();
			message.setEntityId(singleton.getId());
			message.setCommand(Command.REGISTERPLAYER);
			message.setParams(singleton.getUsername());
			Client.getSingleton().send(message);
			playerMap.put(singleton.id, singleton);
		}
		return singleton;
	}

	public static Player getConnectedPlayer(UUID id) {
		Player player = playerMap.get(id);
		if (player == null) {
			RectangleMapObject obj = Player.getPlayerTiledObject();
			player = new Player(obj, null, id);
			playerMap.put(id, player);
		}
		return player;
	}

	public static Map<UUID, Player> getPlayerMap() {
		return playerMap;
	}

	public void processKeyActions() {
		for (GameKeys gk : GameKeys.values()) {
			boolean wasDown = this.getIsKeyDown(gk);
			boolean isPcKeyDown = Gdx.input.isKeyPressed(KeyMapping
					.gameKeyToInt(gk));
			boolean isAndroidKeyDown = getIsAndroidKeyDown(gk);
			boolean isDown = isPcKeyDown || isAndroidKeyDown;
			this.setIsKeyDown(gk, isDown);
			if (!wasDown && isDown) {
				this.keypressed(gk);

				MessageBundle mb = new MessageBundle();
				mb.setEntityId(Player.getSingleton().getId());
				mb.setCommand(Command.KEYPRESSED);
				mb.setParams(gk.toString());
				Client.getSingleton().send(mb);
			} else if (wasDown && !isDown) {
				this.keyreleased(gk);

				MessageBundle mb = new MessageBundle();
				mb.setEntityId(Player.getSingleton().getId());
				mb.setCommand(Command.KEYRELEASED);
				mb.setParams(gk.toString());
				Client.getSingleton().send(mb);
			}
		}
	}

	private boolean getIsAndroidKeyDown(GameKeys gk) {
		boolean result;
		boolean isFirstInRegion = false;
		boolean isSecondInRegion = false;
		boolean isFirstTouched = Gdx.input.isTouched();
		boolean isSecondTouched = Gdx.input.isTouched(1);
		int firstTouchX = Gdx.input.getX();
		int firstTouchY = Gdx.input.getY();
		int secondTouchX = Gdx.input.getX(1);
		int secondTouchY = Gdx.input.getY(1);
		int height = Gdx.graphics.getHeight();
		int width = Gdx.graphics.getWidth();

		switch (gk) {
		case ATTACK:
			break;
		case DOWN:
			isFirstInRegion = firstTouchY > 2 * height / 3;
			isSecondInRegion = secondTouchY > 2 * height / 3;
			break;
		case INTERACT:
			break;
		case JUMP:
			isFirstInRegion = firstTouchX > width / 3
					&& firstTouchY > height / 3 && firstTouchX < 2 * width / 3
					&& firstTouchY < 2 * height / 3;
			isSecondInRegion = secondTouchX > width / 3
					&& secondTouchY > height / 3
					&& secondTouchX < 2 * width / 3
					&& secondTouchY < 2 * height / 3;
			break;
		case LEFT:
			isFirstInRegion = firstTouchX < width / 3;
			isSecondInRegion = secondTouchX < width / 3;
			break;
		case RIGHT:
			isFirstInRegion = firstTouchX > 2 * width / 3;
			isSecondInRegion = secondTouchX > 2 * width / 3;
			break;
		case SELECT:
			break;
		case START:
			break;
		case UP:
			isFirstInRegion = firstTouchY < height / 3;
			isSecondInRegion = secondTouchY < height / 3;
			break;
		default:
			break;

		}
		result = (isFirstTouched && isFirstInRegion)
				|| (isSecondTouched && isSecondInRegion);
		return result;
	}
}

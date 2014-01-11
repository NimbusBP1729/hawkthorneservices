package com.projecthawkthorne.content.nodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.content.Direction;
import com.projecthawkthorne.content.Game;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;
import com.projecthawkthorne.timer.Timeable;
import com.projecthawkthorne.timer.Timer;

public abstract class Enemy extends Humanoid implements Timeable {

	public static enum Behavior {
		Following, Raging, Pacing, GravityObeying, Jumping
	}

	private boolean jumpkill = true;
	private int hp = 1;
	private int damage = 1;
	protected Set<Behavior> behaviors = new HashSet<Behavior>();

	// Following variables
	protected float followSpeed;
	// Raging variables
	protected float rageVelocity = 300 * Game.xFactor;
	// Pacing variables
	protected float paceVelocity = 100 * Game.xFactor;
	protected float maxx;
	protected float minx;
	protected float paceOffset = 24;

	protected long dyingDelay = 750;
	private long reviveDelay = 500;

	public Enemy(RectangleMapObject obj, Level level) {
		super(obj, level);
		try {
			damage = (Integer) obj.getProperties().get("damage");
		} catch (Exception e) {
		}
		try {
			hp = (Integer) obj.getProperties().get("hp");
		} catch (Exception e) {
		}
		if (false == (Boolean) (obj.getProperties().get("jumpkill"))) {
			jumpkill = false;
		}
		maxx = this.x + paceOffset;
		minx = this.x - paceOffset;
	}

	@Override
	protected void collide(Node node) {
		// TODO Auto-generated method stub
		if (!(node instanceof Player)) {
			return;
		}
		Player player = (Player) node;
		if (player.isRebounding() || player.isDead()) {
			return;
		}

		if (player.currentEnemy == null) {
			player.currentEnemy = this;
		}

		if (player.currentEnemy != this) {
			return;
		}

		float[] p_bbox = Bound.FLOAT_ARRAY;
		player.bb.bbox(p_bbox);
		float playerBottom = p_bbox[3];

		float[] e_bbox = Bound.FLOAT_ARRAY;
		this.bb.bbox(e_bbox);
		float enemyTop = e_bbox[1];
		float y2 = e_bbox[3];

		float headsize = (y2 - enemyTop) / 2;

		if (playerBottom >= enemyTop && (playerBottom - enemyTop) < headsize
				&& player.velocityY > this.velocityY && this.jumpkill
				&& this.hp > 0) {
			// if (player.isClimbing()) { // successful attack
			this.hurt(player.jumpDamage);
			if (player.getCheat().isJumpHigh()) {
				player.velocityY = -670 * Player.jumpFactor;
			} else {
				player.velocityY = -450 * Player.jumpFactor;
			}
			return;
		}

		if (player.getCheat().isGod()) {
			this.hurt(this.hp);
			return;
		}

		if (player.isInvulnerable() || this.state == State.DYING) {
			return;
		}

		this.attack();

		player.die(damage);
		// player.bb.move(mtv_x, mtv_y)
		player.velocityY = -450 * Player.jumpFactor;
		int enemyDir = player.x < this.x ? -1 : 1;
		player.velocityX = 300 * Game.xFactor * enemyDir;

	}

	protected void updateVelocity(long dt) {
		Level level = (Level) this.level;
		// TODO: simplify timers move more of its functionality to Node?
		if (this.behaviors.contains(Behavior.GravityObeying)) {
			this.velocityY = this.velocityY + (Game.gravity - this.lift) * dt;
		}

		if (this.behaviors.contains(Behavior.Following) && this.hp > 0) {
			Set<Player> players = level.getPlayers();
			Player player = null;
			Player followPlayer = null;
			float minDist = Float.MAX_VALUE;
			Iterator<Player> it = players.iterator();
			while (it.hasNext()) {
				player = it.next();
				float val = Math.abs(this.x - player.x);
				if (val < minDist) {
					minDist = val;
					followPlayer = player;
				}
			}
			if (followPlayer != null) {
				player = followPlayer;
				float offset = 10;
				if (this.state == State.ATTACK) {
					this.velocityX = 0;
				} else if (this.x < player.x - offset) {
					this.velocityX = this.followSpeed * Game.xFactor;
				} else if (this.x > player.x + offset) {
					this.velocityX = -this.followSpeed * Game.xFactor;
				} else {
					this.velocityX = 0;
				}
			}
		}

		// TODO:test raging, add timers
		if (this.behaviors.contains(Behavior.Raging)
				&& this.state != State.DYINGATTACK && this.hp > 0) {

			int rage_velocity = 1;

			if (this.state == State.ATTACK) {
				rage_velocity = 4;
			}

			Iterator<Player> it = this.playersTouched.iterator();
			while (it.hasNext()) {
				Player player = it.next();

				if (this.state == State.ATTACK) {
					if (this.x < player.x) {
						this.direction = Direction.RIGHT;
					} else if (this.x + this.width > player.x + player.width) {
						this.direction = Direction.LEFT;
					}
				} else {
					if (this.x > this.maxx) {
						this.direction = Direction.LEFT;
					} else if (this.x < this.minx) {
						this.direction = Direction.RIGHT;
					}
				}

				if (this.direction == Direction.LEFT) {
					this.velocityX = -20 * rage_velocity * Game.xFactor;
				} else {
					this.velocityX = 20 * rage_velocity * Game.xFactor;
				}
			}

		} else if (this.behaviors.contains(Behavior.Pacing) && this.hp > 0) {
			if (this.x > this.maxx) {
				this.velocityX = -this.paceVelocity;
			} else if (this.x < this.minx) {
				this.velocityX = this.paceVelocity;
			}
		}

		if (this.velocityX > 0) {
			this.direction = Direction.RIGHT;
		} else if (this.velocityX < 0) {
			this.direction = Direction.LEFT;
		}

	}

	protected void attack() {
		this.state = State.ATTACK;
		this.velocityX = 0;
		Timer.add(1000, "ENEMY_ATTACK", this);
	}

	protected void hurt(int damage) {
		this.state = State.DYING;
		this.hp = this.hp - damage;
		if (this.hp <= 0) {
			Timer.add(this.dyingDelay, "ENEMY_HURT", this);
			// TODO insert in the timer
			if (Timer.contains("ENEMY_REVIVE", this)) {
				Timer.cancel("ENEMY_REVIVE", this);
			}
		} else {
			Timer.add(reviveDelay, "ENEMY_REVIVE", this);
		}
	}

	@Override
	protected void collideEnd(Node node) {
		if (node instanceof Player) {
			Player player = (Player) node;
			if (player.currentEnemy == this) {
				player.currentEnemy = null;
			}
		}
	}

	@Override
	public void floorPushback(Bound floor, float newY) {
		this.y = newY - this.bboxOffsetY - this.bbox_height;
		this.velocityY = 0;
	}

	@Override
	public void wallPushback(Bound bb, float newX, boolean onRight) {
		if (onRight) {
			this.x = newX - this.bboxOffsetX - this.bbox_width;
		} else {
			this.x = newX + this.bboxOffsetX;
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

	public static Enemy create(RectangleMapObject t, Level level) {
		if (t.getName().equals("acorn")) {
			return new Acorn(t, level);
		} else if (t.getName().equals("hippie")) {
			return new Hippie(t, level);
		} else if (t.getName().equals("frog")) {
			return new Frog(t, level);
		} else if (t.getName().equals("fish")) {
			return new Fish(t, level);
		} else {
			throw new UnsupportedOperationException("no enemy found with name:"
					+ t.getName());
		}
	}

	@Override
	public void handleTimer(String name) {
		if (name.equals("ENEMY_HURT")) {
			this.die();
		} else if (name.equals("ENEMY_ATTACK")) {
			if (this.state != State.DYING) {
				this.state = State.DEFAULT;
			}
		}
	}
}

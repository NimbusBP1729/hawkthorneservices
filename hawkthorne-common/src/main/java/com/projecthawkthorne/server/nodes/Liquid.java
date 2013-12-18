package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.server.Game;
import com.projecthawkthorne.server.Player;

public class Liquid extends Node {

	/** true if this liquid drags the player */
	private boolean drag;
	/** true if this liquid can drown the player */
	private boolean drown;
	/** true if the liquid can injure the player */
	private boolean injure;
	/** true if this liquid causes instant death */
	private boolean death;

	public Liquid(RectangleMapObject obj, Gamestate level) {
		super(obj, level);
		if (obj.getName() == null) {
			this.name = "water";
		}
		this.width = obj.getRectangle().width;
		this.height = obj.getRectangle().height;
		this.drag = obj.getProperties().get("drag") == null ? false : (Boolean)(obj.getProperties().get("drag"));
		this.drown = obj.getProperties().get("drown") == null ? false : (Boolean)(obj.getProperties().get("drown"));
		this.injure = obj.getProperties().get("injure") == null ? false : (Boolean)(obj.getProperties().get("injure"));
		this.death = obj.getProperties().get("death") == null ? false : (Boolean)(obj.getProperties().get("death"));
	}

	@Override
	protected void updateVelocity(long dt) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void collide(Node node) {
		if (!(node instanceof Player)) {
			return;
		}
		Player h = (Player) node;
		if (this.death) {
			h.die();
		} else if (this.injure) {
			h.die(1);
		} else if (this.drown && h.y >= this.y) {
			h.die();
		}

		if (this.drag) {
			h.setRebounding(false);
			h.setLiquidDrag(true);
			if (h.velocityX > Game.xFactor * 20) {
				h.velocityX = Game.xFactor * 20;
			} else if (h.velocityX < Game.xFactor * -20) {
				h.velocityX = Game.xFactor * -20;
			}
			if (h.velocityY > 0) {
				h.restore_solid_ground();
				h.setJumping(false);
				h.velocityY = 1 * Player.jumpFactor;
			}
		}

	}

	@Override
	protected void collideEnd(Node node) {
		if (!(node instanceof Player)) {
			return;
		}
		Player p = (Player) node;
		if (this.drag) {
			p.setLiquidDrag(false);
			if (p.velocityY < 0) {
				p.velocityY -= 200 * Player.jumpFactor;
			}
		}
	}
}

package com.projecthawkthorne.content.nodes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;

public class MovingPlatform extends Platform {

	private float[] vertices;
	private float elapsed = 0;
	private float startX;
	private float startY;
	private float speed = 0.05f;
	private Texture texture;
	private float previousX;

	public MovingPlatform(MapObject t, Level level) {
		super(t, level);
		MovementLine movementLine = level.getMovementLines().get(t.getProperties().get("line", String.class));
		Polyline line = movementLine.getLine();
		texture = Assets.loadSprite(t.getProperties().get("sprite", String.class));
		vertices = line.getVertices();
		startX = this.x;
		startY = this.y;
	}
	
	@Override
	protected void updateVelocity(long dt) {
		super.updateVelocity(dt);
		elapsed += dt*speed/1000.0f;
	}	
	
	@Override
	public void update(long dt){
		this.updateVelocity(dt);
		float val = elapsed - MathUtils.floor(elapsed);
		val = val < 0.5 ? 2*val : 2*(1 - val);
		this.previousX = this.x;
		this.x = startX+lerp(vertices[0],vertices[2],val);
		this.y = startY+lerp(vertices[1],vertices[3],val);
		this.moveBoundingBox();
		
		Player player = Player.getSingleton();
		if(player.getCollisionList().contains(this)){
			player.x = player.x + this.x - this.previousX;
		}
	}
	
	private static float lerp(float v0, float v1, float t) {
		return v0+(v1-v0)*t;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(texture, x, y);
	}

}

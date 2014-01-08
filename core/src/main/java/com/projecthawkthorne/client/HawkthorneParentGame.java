package com.projecthawkthorne.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.projecthawkthorne.client.audio.AudioCache;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.socket.MessageBundle;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneParentGame extends Game {
	private SpriteBatch spriteBatch;
	private BatchTiledMapRenderer tileMapRenderer = null;
	private OrthographicCamera cam;
	public static Mode MODE;
	public static final String START_LEVEL = "town";
	public String trackedLevel = START_LEVEL;
	protected Player trackedPlayer;
	protected float trackingX = 0;
	protected float trackingY = 0;
	protected long lastTime = 0;
	protected long lastPositionBroadcast = System.currentTimeMillis();
	protected static final boolean IS_Y_DOWN = false;
	protected MessageBundle mb = new MessageBundle();
	
	

	private long lastIterationInfo = 0;
	private long processingDurationSum = 0;
	private int processingCountSum = 0;
	private int processingIterations = 0;
	private List<Node> liquids = new ArrayList<Node>();


	@Override
	public void create() {
		Assets.load();
		spriteBatch = new SpriteBatch();
		tileMapRenderer = new OrthogonalTiledMapRenderer(null, spriteBatch);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(IS_Y_DOWN, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.zoom = 0.5f;
	}
	
	protected void updateStatus(int msgCount, long processingDuration) {
		processingDurationSum += processingDuration;
		processingCountSum += msgCount;
		processingIterations++;
	}

	protected final void printStatusPeriodically() {
		long now = System.currentTimeMillis();
		if(now-lastIterationInfo > 30000){
			System.out.println("avg. processing duration=="+1.0f*processingDurationSum/processingIterations);
			System.out.println("avg. msg processed      =="+1.0f*processingCountSum/processingIterations);
			System.out.println("iterations              =="+processingIterations);
			System.out.println("================================================");

			lastIterationInfo = now;
		}
	}


	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(IS_Y_DOWN, width, height);
	}

	@Override
	public void render() {
		Timer.updateTimers();
	}

	/**
	 * renders a level with respect to a player or some trackedPosition if a
	 * player is not available
	 * 
	 * @param level
	 * @param player
	 */
	protected void levelRender(Level level, Player player) {
		TiledMap map = level.getTiledMap();
		try {
			float r = Float.parseFloat(map.getProperties().get("red",
					String.class)) / 255.0f;
			float g = Float.parseFloat(map.getProperties().get("green",
					String.class)) / 255.0f;
			float b = Float.parseFloat(map.getProperties().get("blue",
					String.class)) / 255.0f;
			Gdx.gl.glClearColor(r, g, b, 1);
		} catch (NullPointerException e) {
			Gdx.app.error(e.getClass().getName(),
					"Error loading background: default to white");
			Gdx.gl.glClearColor(1, 1, 1, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		float x;
		float y;
		try {
			TiledMapTileLayer tmtl = (TiledMapTileLayer) (map.getLayers()
					.get(0));
			float mapHeight = tmtl.getHeight() * tmtl.getTileHeight();
			float mapWidth = tmtl.getWidth() * tmtl.getTileWidth();

			if (player != null) {
				x = player.x + player.width / 2;
				y = player.y;
				trackingX = x;
				trackingY = y;
			} else {
				x = trackingX;
				y = trackingY;
			}
			cam.position.set(
					limit(x, cam.zoom * cam.viewportWidth / 2, mapWidth
							- cam.zoom * cam.viewportWidth / 2),
					limit(y, cam.zoom * cam.viewportHeight / 2, mapHeight), 0);
		} catch (Exception e) {
			Gdx.app.error(e.getClass().getName(),
					"camera position error: using default (0,0)");
			e.printStackTrace();
			x = 0;
			y = 0;
		}
		cam.update(true);

		spriteBatch.setProjectionMatrix(cam.combined);
		if (!(level.getTiledMap().equals(tileMapRenderer.getMap()))) {
			tileMapRenderer.setMap(level.getTiledMap());
			String musicFile = level.getTiledMap().getProperties()
					.get("soundtrack", String.class);
			AudioCache.playMusic(musicFile);
		}

		tileMapRenderer.setView(cam);
		tileMapRenderer.render();

		spriteBatch.begin();
		try {
			this.draw(level, spriteBatch);
		} catch (Exception e) {
			e.printStackTrace();
		}
		spriteBatch.end();
	}

	public void draw(Level level, SpriteBatch batch) {
		liquids.clear();
		Collection<Node> nodes = level.getNodeMap().values();
		for (Node n : nodes) {
			try {
				if (n instanceof Liquid) {
					liquids.add(n);
				} else {
					n.draw(batch);
				}

			} catch (Exception e) {
				Gdx.app.error("error drawing " + n.getClass(), e.getMessage(),
						e);
			}
		}

		for (Player player : level.getPlayers()) {
			player.draw(batch);
		}
		for (Node liquid : liquids) {
			liquid.draw(batch);
		}
	}

	/**
	 * bounds x between bound1 and bound2
	 * 
	 * @param x
	 * @param bound1
	 * @param bound2
	 * @return
	 */
	private float limit(float x, float bound1, float bound2) {
		if (x < bound1 && x < bound2) {
			return Math.min(bound1, bound2);
		} else if (x > bound1 && x > bound2) {
			return Math.max(bound1, bound2);
		} else {
			return x;
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}

package com.projecthawkthorne.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.Levels;
import com.projecthawkthorne.socket.Client;
import com.projecthawkthorne.socket.Command;
import com.projecthawkthorne.socket.MessageBundle;
import com.projecthawkthorne.socket.Server;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneGame extends Game {
	private SpriteBatch spriteBatch;
	private BatchTiledMapRenderer tileMapRenderer = null;
	private OrthographicCamera cam;
	public static Mode MODE;
	private static final String START_LEVEL = "multiplayer";
	public String trackedLevel = START_LEVEL;
	public Player trackedPlayer = null;
	private float trackingX = 0;
	private float trackingY = 0;
	private long lastTime = 0;
	private long lastPositionBroadcast = System.currentTimeMillis();

	public HawkthorneGame(Mode mode) {
		HawkthorneGame.MODE = mode;
	}

	@Override
	public void create() {
		Assets.load();
		spriteBatch = new SpriteBatch();
		tileMapRenderer = new OrthogonalTiledMapRenderer(null, spriteBatch);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.zoom = 0.5f;
		if (HawkthorneGame.MODE == Mode.CLIENT) {
			Player player = Player.getSingleton();
			Level level = Levels.getSingleton().get(START_LEVEL);
			Levels.switchState(level, level.getDoor("main"), player);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;

		Timer.updateTimers();
		if (HawkthorneGame.MODE == Mode.CLIENT) {
			// TODO:choose how hard to look for packets
			Client client = Client.getSingleton();
			MessageBundle msg = client.receive();
			client.handleMessage(msg);
			Player player = Player.getSingleton();
			Gamestate gs = player.getLevel();
			player.processKeyActions();
			player.update(dt);
			gs.update(dt);
			if (gs instanceof Level) {
				levelRender((Level) gs, player);
			} else {
				throw new UnsupportedOperationException("must be a level");
			}
			if (currentTime - this.lastPositionBroadcast > 50) {
				MessageBundle mb = new MessageBundle();
				mb.setEntityId(player.getId());
				mb.setCommand(Command.POSITION_UPDATE);
				String x = Integer.toString(Math.round(player.x));
				String y = Integer.toString(Math.round(player.y));
				mb.setParams(x, y);
				this.lastPositionBroadcast = currentTime;
				client.send(mb);
			}
		} else if (HawkthorneGame.MODE == Mode.SERVER) {
			// TODO:choose how hard to look for packets
			Server server = Server.getSingleton();
			MessageBundle msg = server.receive();
			server.handleMessage(msg);
			Map<String, Level> levels = Levels.getSingleton().getLevels();
			levelRender(Levels.getSingleton().get(trackedLevel), trackedPlayer);

			for (Level level : levels.values()) {
				Set<Player> players = level.getPlayers();
				for (Player player : players) {
					player.update(dt);
				}
				level.update(dt);
			}

		} else {
			throw new UnsupportedOperationException("unknown mode");
		}
	}

	/**
	 * renders a level with respect to a player or some trackedPosition if a
	 * player is not available
	 * 
	 * @param level
	 * @param player
	 */
	private void levelRender(Level level, Player player) {
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
			if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)
					|| Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) {
				for (Player somePlayer : level.getPlayers()) {
					trackedPlayer = somePlayer;
				}
			}
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				trackedPlayer = null;
			}

			if (player != null) {
				x = player.x + player.width / 2;
				y = player.y;
				trackingX = x;
				trackingY = y;
			} else {
				x = trackingX;
				y = trackingY;

				int boost = 0;
				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
						|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
					boost = 6;
				}
				if (Gdx.input.isKeyPressed(Keys.LEFT)) {
					trackingX -= (5 + boost);
				}
				if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
					trackingX += (5 + boost);
				}
				if (Gdx.input.isKeyPressed(Keys.UP)) {
					trackingY += (5 + boost);
				}
				if (Gdx.input.isKeyPressed(Keys.DOWN)) {
					trackingY -= (5 + boost);
				}
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
		if (Gdx.input.isKeyPressed(Keys.Q) && player != null) {
			System.out.println("camY      =" + y);
			System.out.println("player.x  =" + player.x);
			System.out.println("player.y  =" + player.y);
			System.out.println("player.state  =" + player.getState());
			System.out.println("viewHeight=" + cam.viewportHeight);
			System.out.println();
		}
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)
				&& HawkthorneGame.MODE == Mode.CLIENT) {
			player.die();
		}

		spriteBatch.setProjectionMatrix(cam.combined);
		if (!(level.getTiledMap().equals(tileMapRenderer.getMap()))) {
			tileMapRenderer.setMap(level.getTiledMap());
			String musicFile = level.getTiledMap().getProperties()
					.get("soundtrack", String.class);
			if (HawkthorneGame.MODE == Mode.SERVER) {
				AudioCache.playMusic(musicFile, 0.01f);
			} else {
				AudioCache.playMusic(musicFile);
			}
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
		List<Node> liquids = new ArrayList<Node>();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

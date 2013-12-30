package com.projecthawkthorne.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.GenericGamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class HawkthorneGame extends Game {
	// currently the town is the only file that conforms to new schema
	// i.e. tileset image width and height are powers of 2
	// and uses CSV encoding
	private SpriteBatch spriteBatch;
	private BatchTiledMapRenderer tileMapRenderer = null;
	private OrthographicCamera cam;

	@Override
	public void create() {
		Assets.load();
		spriteBatch = new SpriteBatch();
		tileMapRenderer = new OrthogonalTiledMapRenderer(null, spriteBatch);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.zoom = 0.5f;

	}

	@Override
	public void resize(int width, int height) {
		System.out.println("resizing");
	}

	@Override
	public void render() {
		Gamestate gs = Player.getSingleton().getLevel();
		Player player = Player.getSingleton();
		if (gs instanceof Level) {
			Level level = (Level) gs;
			levelRender(level, player);
		} else if (gs instanceof GenericGamestate) {
			gamestateRender((GenericGamestate) gs, player);
		} else {
			throw new UnsupportedOperationException(
					"must be a level or clientside gamestate");
		}
	}

	private void gamestateRender(GenericGamestate gs, Player player) {

		if (Gdx.input.isKeyPressed(Keys.Q)) {
			System.out.println(Gdx.input.getX() + "," + Gdx.input.getY());
			System.out.println(Gdx.graphics.getWidth() + ","
					+ Gdx.graphics.getHeight());
			System.out.println();
		}

		for (GameKeys gk : GameKeys.values()) {
			boolean oldValue = gs.getIsKeyDown(gk);
			boolean newValue = Gdx.input.isKeyPressed(KeyMapping
					.gameKeyToInt(gk));
			gs.setIsKeyDown(gk, newValue);
			if (!oldValue && newValue) {
				gs.keypressed(gk);
			} else if (oldValue && !newValue) {
				gs.keyreleased(gk);
			}
		}
		Gdx.gl.glClearColor(0, 1, 0, 1);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		cam.position.set(cam.zoom * Gdx.graphics.getWidth() / 2, cam.zoom
				* Gdx.graphics.getHeight() / 2, 0);
		// update should do nothing in unmoving gamestates
		cam.update();
		gs.update();
		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		this.draw(gs, spriteBatch);
		spriteBatch.end();
	}

	private void draw(GenericGamestate gs, SpriteBatch spriteBatch) {
		for (RadioButtonGroup elem : gs.getObjects()) {
			elem.draw(spriteBatch);
		}

	}

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
			System.err.println("Error loading background: default to white");
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

			x = player.x + player.width / 2;
			y = player.y;
			cam.position.set(
					limit(x, cam.zoom * cam.viewportWidth / 2, mapWidth
							- cam.zoom * cam.viewportWidth / 2),
					limit(y, cam.zoom * cam.viewportHeight / 2, mapHeight), 0);
		} catch (Exception e) {
			System.err.println("camera position error: using default (0,0)");
			e.printStackTrace();
			x = 0;
			y = 0;
		}
		cam.update(true);
		if (Gdx.input.isKeyPressed(Keys.Q)) {
			System.out.println("camY      =" + y);
			System.out.println("player.x  =" + player.x);
			System.out.println("player.y  =" + player.y);
			System.out.println("viewHeight=" + cam.viewportHeight);
			System.out.println();
		}

		for (GameKeys gk : GameKeys.values()) {
			boolean oldValue = player.getIsKeyDown(gk);
			boolean newValue = Gdx.input.isKeyPressed(KeyMapping
					.gameKeyToInt(gk));
			player.setIsKeyDown(gk, newValue);
			if (!oldValue && newValue) {
				player.keypressed(gk);
			} else if (oldValue && !newValue) {
				player.keyreleased(gk);
			}
		}

		level.update();

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
		this.draw(level, spriteBatch);
		spriteBatch.end();
	}

	public void draw(Level level, SpriteBatch batch) {
		List<Node> liquids = new ArrayList<Node>();
		Iterator<com.projecthawkthorne.content.nodes.Node> nit = level
				.getNodes().values().iterator();
		while (nit.hasNext()) {
			Node n = nit.next();
			if (n instanceof Liquid) {
				liquids.add(n);
			} else {
				n.draw(batch);
			}
		}

		Player.getSingleton().draw(batch);

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

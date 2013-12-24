package com.projecthawkthorne.client;

import static com.projecthawkthorne.gamestate.Level.SRC_MAPS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.projecthawkthorne.client.audio.AudioCache;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;
import com.projecthawkthorne.content.Player;
//import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Levels;

public class HawkthorneGame extends Game {
	// currently the town is the only file that conforms to new schema
	// i.e. tileset image width and height are powers of 2
	// and uses CSV encoding
	private SpriteBatch spriteBatch;
	private TiledMap map;
	private TiledMapRenderer tileMapRenderer = null;
	private OrthographicCamera cam;
	private OrthographicCamera mapCam;
	private int offset;

	@Override
	public void create() {
		Assets.load();

		spriteBatch = new SpriteBatch();
		Player player = Player.getSingleton();
		stateSwitch("overworld", player.getLevel().getName());

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		float camX;
		float camY;
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
		Player player = Player.getSingleton();
		try {
			// TODO: reimplement client
			camX = player.x + player.width / 2;
			// TODO: implement panning
			int pan = 0;
			camY = limit(limit(player.y, 0, offset) + pan, 0, offset);
		} catch (Exception e) {
			System.err.println("camera position error: using default (0,0)");
			e.printStackTrace();
			camX = 0;
			camY = 0;
		}
		TiledMapTileLayer tmtl = (TiledMapTileLayer) (map.getLayers().get(0));
		int mapHeight = Math.round(tmtl.getHeight() * tmtl.getTileHeight());
		int mapWidth = Math.round(tmtl.getWidth() * tmtl.getTileHeight());
		camX = limit(camX, cam.zoom * cam.viewportWidth / 2, mapWidth
				- cam.zoom * cam.viewportWidth / 2);
		if (camY >= offset * cam.zoom * tmtl.getTileHeight() * 2) {
			camY = offset * cam.zoom * tmtl.getTileHeight();
		} else {
			camY = camY - mapHeight * cam.zoom;
		}
		if (Gdx.input.isKeyPressed(Keys.Q)) {
			System.out.println("camY      =" + camY);
			System.out.println("player.x  =" + player.x);
			System.out.println("player.y  =" + player.y);
			System.out.println("offset    =" + offset);
			System.out.println("viewHeight=" + cam.viewportHeight);
			System.out.println("mapHeight =" + mapHeight);
			System.out.println("tileHeight=" + tmtl.getTileHeight());
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

		// cam.position.set(tileMapRenderer.getMapWidthUnits() / 2,
		// tileMapRenderer.getMapHeightUnits() / 2, 0);
		cam.position.set(camX, camY + mapHeight / 2, 0);
		mapCam.position.set(camX, mapHeight / 2 - camY, 0);
		cam.update(true);
		mapCam.update(true);

		// TODO: reimplement this
		// only tracking one player
		// and only tracking that player's level
		Gamestate level = Levels.getSingleton()
				.get(player.getLevel().getName());
		level.update();
		//
		// // receive a new bundle
		// DatagramPacket bundle = client.receive();
		//
		// String msg;
		// while (bundle != null) {
		// msg = new String(bundle.getData());
		// // process bundle if necessary
		// processBundle(bundle);
		// bundle = client.receive();
		// }

		spriteBatch.setProjectionMatrix(cam.combined);
		if (tileMapRenderer == null) {
			tileMapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
		}
		tileMapRenderer.setView(mapCam);
		tileMapRenderer.render();

		spriteBatch.begin();
		this.draw(spriteBatch, cam);
		spriteBatch.end();
	}

	public void draw(SpriteBatch batch, OrthographicCamera cam) {
		long curTime = System.currentTimeMillis();
		List<Node> liquids = new ArrayList<Node>();
		Iterator<com.projecthawkthorne.content.nodes.Node> nit = Levels
				.getSingleton().get(Player.getSingleton().getLevel().getName())
				.getNodes().values().iterator();
		while (nit.hasNext()) {
			Node n = nit.next();
			if ("liquid".equals(n.type)) {
				liquids.add(n);
			} else {
				Assets.draw(batch, n);
			}
		}

		Assets.draw(batch, Player.getSingleton());

		for (Node liquid : liquids) {
			Assets.draw(batch, liquid);
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

	private void stateSwitch(String fromLevel, String toLevel) {

		long startTime, endTime;

		startTime = System.currentTimeMillis();
		String mapFileName = SRC_MAPS + toLevel + ".tmx";
		AssetManager assetManager = new AssetManager();
		// TODO:bypass if it's not an ordinary level
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		assetManager.load(mapFileName, TiledMap.class);
		assetManager.finishLoading();
		map = assetManager.get(mapFileName);
		endTime = System.currentTimeMillis();
		System.out.println("Loaded map " + mapFileName + " in "
				+ (endTime - startTime) + "ms");

		String musicFile = (String) map.getProperties().get("soundtrack");
		AudioCache.playMusic(musicFile);

		// float aspectRatio = (float)Gdx.graphics.getWidth() /
		// (float)Gdx.graphics.getHeight();
		// cam = new OrthographicCamera(100f * aspectRatio, 100f);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		mapCam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mapCam.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		cam.zoom = 0.5f;
		mapCam.zoom = 0.5f;

		try {
			offset = Integer.parseInt(map.getProperties().get("offset",
					String.class));
		} catch (Exception e) {
			System.err.println("no offset found: using default '0'");
			offset = 0;
		}
		// offset -= map.getProperties().get("height", Integer.class);
		System.out.println("resolved offset is:" + offset);

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

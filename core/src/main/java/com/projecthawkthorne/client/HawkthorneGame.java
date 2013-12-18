package com.projecthawkthorne.client;

import java.net.DatagramPacket;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.projecthawkthorne.client.audio.AudioCache;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.client.display.Node;
import com.projecthawkthorne.client.display.Player;
import com.projecthawkthorne.socket.Client;

public class HawkthorneGame extends Game {
	// currently the town is the only file that conforms to new schema
	// i.e. tileset image width and height are powers of 2
	// and uses CSV encoding
	public static final String START_LEVEL = "multiplayer";
	public static boolean DEBUG = true;
	Client client = Client.getSingleton();
	private BitmapFont font;
	private SpriteBatch spriteBatch;
	private TiledMap map;
	private TiledMapRenderer tileMapRenderer = null;
	private OrthographicCamera cam;
	private OrthographicCamera mapCam;
	public static String SRC_MAPS = "../data/maps/";
	private int offset;

	@Override
	public void create() {
		Assets.load();

		font = new BitmapFont();
		font.setColor(Color.RED);
		Gdx.files.internal(Node.IMAGES_FOLDER + "defaultObject.png");

		spriteBatch = new SpriteBatch();

		stateSwitch("overworld", START_LEVEL);

	}

	@Override
	public void resize(int width, int height) {
		// spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	@Override
	public void render() {
		float camX;
		float camY;
		try {
			float r = Float.parseFloat((String) map.getProperties().get("red")) / 255.0f;
			float g = Float.parseFloat((String) map.getProperties()
					.get("green")) / 255.0f;
			float b = Float
					.parseFloat((String) map.getProperties().get("blue")) / 255.0f;
			Gdx.gl.glClearColor(r, g, b, 1);
		} catch (NullPointerException e) {
			System.err.println("Error loading background: default to white");
			Gdx.gl.glClearColor(1, 1, 1, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		try {
			Player player = this.client.players.get(this.client.getEntity());
			camX = player.getX() + player.width / 2;
			// TODO: implement panning
			int pan = 0;
			camY = player.getY();// limit( limit(y, 0, offset) + pan, 0, offset
									// );
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
		if (camY > offset * cam.zoom * tmtl.getTileHeight() * 2) {
			camY = offset * cam.zoom * tmtl.getTileHeight();
		} else {
			camY = camY - mapHeight * cam.zoom;
		}
		if (Gdx.input.isKeyPressed(Keys.Q)) {
			System.out.println("camY      ==" + camY);
			System.out.println("player,y  =="
					+ this.client.players.get(this.client.getEntity()).getY());
			System.out.println("offset    ==" + offset);
			System.out.println("viewHeight==" + cam.viewportHeight);
			System.out.println("mapHeight ==" + mapHeight);
			System.out.println("tileHeight==" + tmtl.getTileHeight());
			System.out.println();
		}
		// cam.position.set(tileMapRenderer.getMapWidthUnits() / 2,
		// tileMapRenderer.getMapHeightUnits() / 2, 0);
		cam.position.set(camX, camY + mapHeight / 2, 0);
		mapCam.position.set(camX, mapHeight / 2 - camY, 0);
		cam.update(true);
		mapCam.update(true);

		client.update();

		// receive a new bundle
		DatagramPacket bundle = client.receive();

		String msg;
		while (bundle != null) {
			msg = new String(bundle.getData());
			if (msg.contains("poly")) {
				int foo = 47;
			} else if (msg.contains("player")) {
				int foo = 53;
			}
			// process bundle if necessary
			processBundle(bundle);
			bundle = client.receive();
		}

		if (tileMapRenderer == null) {
			tileMapRenderer = new OrthogonalTiledMapRenderer(map);
		}
		//tileMapRenderer.render(mapCam);
		tileMapRenderer.render();

		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		client.draw(spriteBatch, cam);
		spriteBatch.end();
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

	private void processBundle(DatagramPacket bundle) {
		if (bundle == null) {
			return;
		}
		byte[] msg = bundle.getData();
		String[] tokens = new String(msg).split("\\s+", 3);
		String entity = tokens[0];
		String cmd = tokens[1];
		String params = tokens[2].trim();
		if (cmd.equals("updatePlayer")) {
			Player.unpack(this.client.players, params);
		} else if (cmd.equals("updateObject")) {
			Node.unpack(this.client.world, params);
		} else if (cmd.equals("stateSwitch")) {
			String[] chunks = params.split(" ");
			String fromLevel = chunks[0];
			String toLevel = chunks[1];
			if (entity.equals(this.client.getEntity())) {
				stateSwitch(fromLevel, toLevel);
			}
			// TODO:confirm it's a player
			this.client.players.get(entity).levelName = toLevel;

		} else if (cmd.equals("sound")) {
			AudioCache.playSfx(params);
		} else {
			System.err.println("Unknown command:" + cmd);
		}

	}

	private void stateSwitch(String fromLevel, String toLevel) {
		if (!this.client.world.containsKey(toLevel)) {
			this.client.world.put(toLevel, new HashMap<String, Node>());
		}
		this.client.setLevel(toLevel);

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

		startTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Created cache in " + (endTime - startTime) + "ms");

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
			offset = Integer.parseInt((String) map.getProperties()
					.get("offset"));
		} catch (Exception e) {
			System.err.println("no offset found: using default '0'");
			offset = 0;
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

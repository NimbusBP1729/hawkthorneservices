package com.projecthawkthorne.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Boundary;
import com.projecthawkthorne.content.Game;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Floor;
import com.projecthawkthorne.content.nodes.Ladder;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.content.nodes.Platform;
import com.projecthawkthorne.hardoncollider.Bound;
import com.projecthawkthorne.hardoncollider.Collidable;
import com.projecthawkthorne.hardoncollider.Collider;

/**
 * 
 * @author Patrick
 */
public class Level extends Gamestate {

	private static final boolean IS_Y_DOWN = false;
	private String title;
	private Map<UUID, Node> nodes = new HashMap<UUID, Node>();
	private Level spawnLevel;
	private final String name;
	private java.util.Map<String, Door> doors = new HashMap<String, Door>();
	private Boundary boundary = new Boundary();
	private TiledMap tiledMap;
	private Collider collider;
	private List<Node> liquids = new ArrayList<Node>();
	private float trackingX = 0;
	private float trackingY = 0;
	private BatchTiledMapRenderer tileMapRenderer;
	private OrthographicCamera cam;
	private static Map<String, Level> levelMap = new HashMap<String,Level>();


	public static Map<String, Level> getLevelMap() {
		return levelMap;
	}

	private Level(String name) {
		this.name = name;
		this.collider = new Collider();
		this.loadNodes(name);
		tileMapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap);
		this.spawnLevel = this;
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(IS_Y_DOWN);
		cam.zoom = 0.5f;
	}
		
	public static Level get(String name) {
		Level level = Level.getLevelMap().get(name);
		if(level == null){
			level = new Level(name);
			Level.levelMap.put(name, level);
		}
		return level;
	}

	private void loadNodes(String levelName) {
		
		this.tiledMap = Assets.getTiledMap(levelName.trim());
		MapProperties prop = tiledMap.getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);

		this.boundary.width = mapWidth * tilePixelWidth;
		this.boundary.height = mapHeight * tilePixelHeight;

		// floor is deprecated
		MapLayer floorGroup = this.getNodeGroupByName("floor");
		if (floorGroup != null) {
			for (MapObject t : floorGroup.getObjects()) {
				Node node;
				t.getProperties().put("level", levelName);
				node = new Floor((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			}
		}

		MapLayer wallGroup = this.getNodeGroupByName("wall");
		if (wallGroup != null) {
			for (MapObject t : wallGroup.getObjects()) {
				Node node;
				t.getProperties().put("level", levelName);
				node = new Floor((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			}
		}

		MapLayer platformGroup = this.getNodeGroupByName("platform");
		if (platformGroup != null) {
			for (MapObject t : platformGroup.getObjects()) {
				Node node;
				t.getProperties().put("level", levelName);
				node = new Platform((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			}
		}

		MapLayer nodeGroup = this.getNodeGroupByName("nodes");
		for (MapObject t : nodeGroup.getObjects()) {
			Node node = null;
			String typeName = t.getProperties().get("type", String.class);
			try {
				if ("liquid".equals(typeName)) {
					node = new Liquid((RectangleMapObject) t, this);
				} else if ("door".equals(typeName)) {
					node = new Door((RectangleMapObject) t, this);
				} else if ("ladder".equals(typeName)) {
					node = new Ladder((RectangleMapObject) t, this);
				} else {
					Gdx.app.error("typeName is not recognized:", typeName);
				}
			} catch (Exception e) {
				Gdx.app.error("error loading a type", typeName);
			}
			this.nodes.put(node.getId(), node);
			if (node instanceof Door) {
				this.doors.put(node.name, (Door) node);
			}
		}

	}

	private MapLayer getNodeGroupByName(String name) {
		return this.tiledMap.getLayers().get(name);
	}

	@Override
	public Map<UUID, Node> getNodeMap() {
		return nodes;
	}

	public String getName() {
		return name;
	}

	public void playerKeyPressed(Player player, String key) {
		Iterator<Collidable> it = player.getCollisionList().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			GameKeys button = GameKeys.valueOf(key);
			node.setIsKeyDown(button, true);
		}
	}

	/**
	 * @return the world
	 */
	public Collider getCollider() {
		return collider;
	}

	@Override
	public Door getDoor(String doorName) {
		try {
			return this.doors.get(doorName);
		} catch (Exception e) {
			return null;
		}
	}

	public Boundary getBoundary() {
		return boundary;
	}

	/**
	 * @return the spawnLevel
	 */
	public Level getSpawnLevel() {
		return spawnLevel;
	}

	
	/**
	 * if door is null, the player ends up in his current location
	 * 
	 * @param newLevel
	 *            the destination level
	 * @param door
	 *            the door in the destination level
	 * @param player
	 *            the player being transported
	 */
	public static void switchState(Gamestate newLevel, Door door, Player player) {
		Gamestate oldLevel = player.getLevel();
		if (oldLevel != null) {
			oldLevel.removePlayer(player);
		}
		player.setLevel(newLevel);
		player.stopJumping();
		player.getJumpQueue().flush();
		player.getCharacter().reset();
		player.velocityY = player.velocityX = 0;
		if (door != null) {
			player.x = door.x + door.width / 2 - player.width / 2;
			player.y = door.y;
		}
		newLevel.addPlayer(player);

		Bound bb = player.getBb();
		if (oldLevel instanceof Level) {
			if (bb != null) {
				((Level) oldLevel).getCollider().removeBox(bb);
			}
		}

		if (newLevel instanceof Level) {
			if (bb == null) {
				player.setBb(Bound.create(player.x, player.y, 18, 44));
				bb = player.getBb();
			}
			((Level) newLevel).getCollider().addBox(bb);
			// this.moveBoundingBox();
			// this.attack_box = PlayerAttack.new(collider,self);;
		}
		
		context.setScreen(newLevel);

	}

	@Override
	public void render(float delta) {
		long dt = (long) (delta*1000);
		Player player = Player.getSingleton();
		if (Gdx.input.isKeyPressed(Keys.DEL)) {
			player.die();
		}
		player.processKeyActions();
		Set<Player> players = this.getPlayers();
		for (Player p : players) {
			p.update(dt);
		}
		for(Node node : this.nodes.values()){
			node.update(dt);
		}
		this.collider.update();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(IS_Y_DOWN, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		this.resume();
	}

	@Override
	public void hide() {
		String musicFile = this.tiledMap.getProperties()
                .get("soundtrack", String.class);
		Assets.stopMusic(musicFile);
	}

	@Override
	public void pause() {
		String musicFile = this.tiledMap.getProperties()
                .get("soundtrack", String.class);
		Assets.stopMusic(musicFile);
		context.setScreen(GenericGamestate.get("pause"));
	}

	@Override
	public void resume() {
		this.tiledMap = Assets.getTiledMap(name);
		String musicFile = this.tiledMap.getProperties()
	                        .get("soundtrack", String.class);
	    Assets.playMusic(musicFile);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.setProjectionMatrix(cam.combined);
		trackPlayerWithCam(Player.getSingleton(), cam);
		TiledMap map = this.tiledMap;
		tileMapRenderer.setView(cam);
		tileMapRenderer.setMap(map);
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

		batch.end();
		tileMapRenderer.render();
		batch.begin();

		liquids.clear();
		for (Node n : this.getNodeMap().values()) {
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
		for (Player player : this.getPlayers()) {
			player.draw(batch);
		}
		for (Node liquid : liquids) {
			liquid.draw(batch);
		}
		if(Game.DEBUG){
			this.collider.draw();
		}
	}

	private void trackPlayerWithCam(Player player, OrthographicCamera cam) {
		TiledMapTileLayer tmtl = (TiledMapTileLayer) this.tiledMap.getLayers().get(0);
		float mapHeight = tmtl.getHeight() * tmtl.getTileHeight();
		float mapWidth = tmtl.getWidth() * tmtl.getTileWidth();

		float x;
		float y;
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
				MathUtils.clamp(
						x
						, cam.zoom * cam.viewportWidth / 2
						, mapWidth - cam.zoom * cam.viewportWidth / 2)
				, MathUtils.clamp(
						y
						, cam.zoom * cam.viewportHeight / 2
						, mapHeight)
				, 0);
		cam.update(true);
	}

}
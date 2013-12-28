package com.projecthawkthorne.gamestate;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.projecthawkthorne.content.Boundary;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Enemy;
import com.projecthawkthorne.content.nodes.Floor;
import com.projecthawkthorne.content.nodes.Ladder;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.Material;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.content.nodes.Platform;
import com.projecthawkthorne.hardoncollider.Collidable;
import com.projecthawkthorne.hardoncollider.Collider;

/**
 * 
 * @author Patrick
 */
public class Level extends Gamestate {

	public static final String SRC_MAPS = "../data/maps/";
	private String title;
	private LevelMap nodes = new LevelMap();
	private Gamestate spawnLevel;
	private final String name;
	private java.util.Map<String, Door> doors = new HashMap<String, Door>();
	private Boundary boundary = new Boundary();
	private long lastTime = 0;
	private com.badlogic.gdx.maps.Map map;
	private Collider collider;

	public Level(String name) {
		this.name = name;
		this.collider = new Collider();
		this.loadNodes(name);

		this.spawnLevel = this;
	}

	private void loadNodes(String levelName) {
		TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());

		this.map = loader.load(SRC_MAPS + levelName.trim() + ".tmx");
		MapProperties prop = map.getProperties();
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
			Node node;
			if ("material".equals(t.getProperties().get("type", String.class))) {
				node = new Material((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			} else if ("door".equals(t.getProperties()
					.get("type", String.class))) {
				node = new Door((RectangleMapObject) t, this);
				this.doors.put(node.name, (Door) node);
			} else if ("enemy".equals(t.getProperties().get("type",
					String.class))) {
				// TODO: remove this chunk of code:
				// i.e. deprecate 'enemytype' and have it
				// represented as a 'name' in the .tmx file
				if (t.getProperties().get("enemytype") != null) {
					t.setName(t.getProperties().get("enemytype", String.class));
				}
				node = Enemy.create((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			} else if ("climbable".equals(t.getProperties().get("type",
					String.class))) {
				node = new Ladder((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			} else if ("liquid".equals(t.getProperties().get("type",
					String.class))) {
				node = new Liquid((RectangleMapObject) t, this);
				this.nodes.put(node.getId(), node);
			} else {
				System.err.println("Unknown type:"
						+ t.getProperties().get("type"));
			}
		}

	}

	private MapLayer getNodeGroupByName(String name) {
		return this.map.getLayers().get(name);
	}

	@Override
	public LevelMap getNodes() {
		return nodes;
	}

	public String getName() {
		return name;
	}

	@Override
	public void update() {
		// 1)update

		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		Player player = Player.getSingleton();
		player.update(dt);
		Iterator<Node> nit = this.nodes.values().iterator();
		while (nit.hasNext()) {
			try {
				Node node = nit.next();
				node.update(dt);
			} catch (ConcurrentModificationException cme) {
				// don't update a node if it's been changed
				cme.printStackTrace();
			}
		}

		this.collider.update();
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
	public Gamestate getSpawnLevel() {
		return spawnLevel;
	}

}
package com.projecthawkthorne.gamestate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.projecthawkthorne.content.Boundary;
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

	public static final String SRC_MAPS = "data/maps/";
	private String title;
	private Map<UUID, Node> nodes = new HashMap<UUID, Node>();
	private Level spawnLevel;
	private final String name;
	private java.util.Map<String, Door> doors = new HashMap<String, Door>();
	private Boundary boundary = new Boundary();
	private TiledMap tiledMap;
	private Collider collider;
	private static Map<String, Level> levelMap = new HashMap<String,Level>();


	public static Map<String, Level> getLevelMap() {
		return levelMap;
	}

	private Level(String name) {
		this.name = name;
		this.collider = new Collider();
		this.loadNodes(name);

		this.spawnLevel = this;
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
		TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());

		this.tiledMap = loader.load(SRC_MAPS + levelName.trim() + ".tmx");
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

	@Override
	public void update(long dt) {
		Iterator<Node> nit = this.nodes.values().iterator();
		while (nit.hasNext()) {
			Node node = nit.next();
			node.update(dt);
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
	public Level getSpawnLevel() {
		return spawnLevel;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
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

	}
}
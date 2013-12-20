package com.projecthawkthorne.gamestate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.projecthawkthorne.content.Boundary;
import com.projecthawkthorne.content.Game;
import com.projecthawkthorne.content.Keys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Enemy;
import com.projecthawkthorne.content.nodes.Floor;
import com.projecthawkthorne.content.nodes.Ladder;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.Material;
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

	public static final String SRC_MAPS = "../data/maps/";
	private static final boolean IGNORE_GUI = true;
	private String title;
	private LevelMap nodes = new LevelMap();
	private Set<Player> players = new HashSet<Player>();
	// private Collider collider;
	private Gamestate spawnLevel;
	private final String name;
	private java.util.Map<String, Door> doors = new HashMap<String, Door>();
	private Boundary boundary = new Boundary();
	private long lastTime = 0;
	private com.badlogic.gdx.maps.Map map;
	private Collider collider;

	// private SpriteBatch batch = new SpriteBatch();
	// private BitmapFont font = new BitmapFont();
	// private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer( true,
	// true, true, true, true);

	public Level(String name) {
		// this.world.enableRestingBodyDetection(0.01f, (float)Math.PI/16,
		// 0.01f);
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

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
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

	public void update() {
		// 1)update

		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		Iterator<Player> pit = this.players.iterator();
		while (pit.hasNext()) {
			Player player = pit.next();
			player.update(dt);
		}
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

		// batch.begin();
		// //BoxObjectManager.GetWorld() gets the reference to Box2d World
		// object
		// if(this.world!=null && Main.camera!=null &&
		// Main.camera.combined!=null){
		// debugRenderer.render(this.world, Main.camera.combined);
		// }
		// batch.end();
		// finds more collisions
		// this.world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS,
		// BOX_POSITION_ITERATIONS);
		// this.checkCollisions();
		this.collider.update();

		// 2)draw

		// this.draw();

		// 3)user I/O
	}

	public void draw() {
		if (IGNORE_GUI) {
			return;
		} else {
			Gdx.gl.glClearColor(0, 0, 0.6f, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			// batch.begin();
			// font.draw(batch, Gdx.graphics.getFramesPerSecond() + "", 100,
			// 100);
			// batch.end();

			Game.DEBUG = true;
			// grphcs.clear();
			Iterator<Bound> it = this.collider.getBoxes();
			while (it.hasNext()) {
				it.next().draw();
			}
		}
		//
	}

	public void playerKeyPressed(Player player, String key) {
		Iterator<Collidable> it = player.getCollisionList().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			Keys button = Keys.valueOf(key);
			node.setKeyDown(button, true);
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
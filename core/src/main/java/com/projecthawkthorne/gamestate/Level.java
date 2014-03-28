package com.projecthawkthorne.gamestate;

import static com.projecthawkthorne.client.HawkthorneGame.HEIGHT;
import static com.projecthawkthorne.client.HawkthorneGame.WIDTH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Boundary;
import com.projecthawkthorne.content.Footprint;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Floor;
import com.projecthawkthorne.content.nodes.Ladder;
import com.projecthawkthorne.content.nodes.Liquid;
import com.projecthawkthorne.content.nodes.MovementLine;
import com.projecthawkthorne.content.nodes.MovingPlatform;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.content.nodes.Platform;
import com.projecthawkthorne.hardoncollider.Bound;
import com.projecthawkthorne.hardoncollider.Collidable;
import com.projecthawkthorne.hardoncollider.Collider;
import com.projecthawkthorne.socket.udp.Command;
import com.projecthawkthorne.socket.udp.MessageBundle;

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
	private java.util.Map<String, MovementLine> movementLines = new HashMap<String, MovementLine>();
	public java.util.Map<String, MovementLine> getMovementLines() {
		return movementLines;
	}

	private Boundary boundary = new Boundary();
	private TiledMap tiledMap;
	private Collider collider;
	private List<Node> liquids = new ArrayList<Node>();
	private float trackingX = 0;
	private float trackingY = 0;
	private BatchTiledMapRenderer tileMapRenderer;
	private OrthographicCamera cam;
	private static SpriteBatch batch;
	private boolean isFloorSpace;
	private static Map<String, Level> levelMap = new HashMap<String,Level>();
	
	private static final Player[] EMPTY_PLAYER_ARRAY = new Player[0];
	boolean switchCharacterKeyDown = false;
	private int trackedPlayerIndex = 0;



	public static Map<String, Level> getLevelMap() {
		return levelMap;
	}

	private Level(String name) {
		this.name = name;
		this.collider = new Collider();
		
		batch = context.getBatch();
		
		this.loadNodes(name);
		this.spawnLevel = this;
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.setToOrtho(IS_Y_DOWN);
		switch(Gdx.app.getType()){
		case Android:
			cam.zoom = 0.25f;
			break;
		default:
			cam.zoom = 0.5f;
			break;
		}
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
		tileMapRenderer = new OrthogonalTiledMapRenderer( tiledMap, batch);

		MapProperties prop = tiledMap.getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);
		this.isFloorSpace = this.getNodeGroupByName("floorspace")!=null;

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
		
		// floor is deprecated
		MapLayer blockGroup = this.getNodeGroupByName("block");
		if (blockGroup != null) {
			for (MapObject t : blockGroup.getObjects()) {
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
				node = new Platform(t, this);
				this.nodes.put(node.getId(), node);
			}
		}

		MapLayer movementGroup = this.getNodeGroupByName("movement");
		if (movementGroup != null) {
			for (MapObject t : movementGroup.getObjects()) {
				MovementLine node;
				t.getProperties().put("level", levelName);
				node = new MovementLine((PolylineMapObject) t);
				this.movementLines.put(node.name, node);
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
				} else if ("climbable".equals(typeName)) {
					node = new Ladder((RectangleMapObject) t, this);
				} else if ("movingplatform".equals(typeName)) {
					node = new MovingPlatform((RectangleMapObject) t, this);
				} else {
					Gdx.app.error("node is not recognized", typeName);
					continue;
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
		System.out.println("level = "+newLevel.getName());
		Gamestate oldLevel = player.getLevel();
		if (oldLevel != null) {
			oldLevel.removePlayer(player);
		}
		player.setLevel(newLevel);
		player.stopJumping();
		player.getJumpQueue().flush();
		player.getCharacter().reset();
		player.velocityY = player.velocityX = 0;
		player.getCollisionList().clear();
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
			player.setFootprint(((Level) newLevel).isFloorSpace ? new Footprint() : null);
			// this.moveBoundingBox();
			// this.attack_box = PlayerAttack.new(collider,self);;
		}

		if (HawkthorneGame.MODE == Mode.CLIENT 
				&& player.getId() == Player.getSingleton().getId()) {
			
			context.setScreen(newLevel);
			MessageBundle mb = new MessageBundle();
			mb.setEntityId(Player.getSingleton().getId());
			mb.setCommand(Command.SWITCHLEVEL);
			mb.setParams(newLevel.getName(), door.name);
			player.getClient().send(mb);
		}

	}

	@Override
	public void update(float delta) {
		long dt = (long) (delta*1000);
		tileMapRenderer.setView(cam);
		if (Gdx.input.isKeyPressed(Keys.DEL) && HawkthorneGame.MODE == Mode.CLIENT) {
			Player player = Player.getSingleton();
			player.die();
		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			context.setScreen(context.pause);
		}
		if (HawkthorneGame.MODE == Mode.CLIENT) {
			Player player = Player.getSingleton();
			player.processKeyActions();
		}
		
		for(Node node : this.nodes.values()){
			node.update(dt);
		}
		
		if(HawkthorneGame.MODE == Mode.SERVER){
			if (!switchCharacterKeyDown 
					&& Gdx.input.isKeyPressed(Keys.S)
					&& this.getPlayers().size()>0) {
				Player[] players = this.getPlayers().toArray(EMPTY_PLAYER_ARRAY);
				trackedPlayerIndex = (trackedPlayerIndex+1)%players.length;
				context.trackedPlayer = players[trackedPlayerIndex];
			}
			switchCharacterKeyDown = Gdx.input.isKeyPressed(Keys.S);
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				if(context.trackedPlayer!=null){
					context.trackedLevel = context.trackedPlayer.getLevel().getName();
				}
				context.trackedPlayer = null;
			}
			int boost = 0;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
					|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
				boost = 6;
			}
			if (Gdx.input.isKeyPressed(Keys.LEFT) 
					|| context.getUserInterface().getIsAndroidKeyDown(GameKeys.LEFT)) {
				trackingX -= (5 + boost);
			}
			if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| context.getUserInterface().getIsAndroidKeyDown(GameKeys.RIGHT)) {
				trackingX += (5 + boost);
			}
			if (Gdx.input.isKeyPressed(Keys.UP)
				|| context.getUserInterface().getIsAndroidKeyDown(GameKeys.UP)) {
				trackingY += (5 + boost);
			}
			if (Gdx.input.isKeyPressed(Keys.DOWN)
					|| context.getUserInterface().getIsAndroidKeyDown(GameKeys.DOWN)) {
				trackingY -= (5 + boost);
			}

		} 
		
		this.collider.update();
	}

	@Override
	public void resize(int width, int height) {
		//cam.setToOrtho(IS_Y_DOWN, width, height);
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
		//context.setScreen("pause");
	}

	@Override
	public void resume() {
		this.tiledMap = Assets.getTiledMap(name);
		this.tileMapRenderer= new OrthogonalTiledMapRenderer(this.tiledMap, batch);
		String musicFile = this.tiledMap.getProperties()
	                        .get("soundtrack", String.class);
	    Assets.playMusic(musicFile);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * renders a level with respect to a player or some trackedPosition if a
	 * player is not available
	 * 
	 * @param player
	 */
	public void draw(Player player) {
		TiledMap map = tiledMap;
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

		batch.setProjectionMatrix(cam.combined);
		if (!(this.getTiledMap().equals(tileMapRenderer.getMap()))) {
			tileMapRenderer.setMap(this.getTiledMap());
			String musicFile = this.getTiledMap().getProperties()
					.get("soundtrack", String.class);
			Assets.playMusic(musicFile);
		}

		tileMapRenderer.setView(cam);
		tileMapRenderer.render();

		batch.begin();
		this.drawEntities(batch);
		batch.end();

	}

	public void drawEntities(SpriteBatch batch) {
		liquids.clear();
		Collection<Node> nodes = this.getNodeMap().values();
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

		for (Player player : this.getPlayers()) {
			player.draw(batch);
		}
		for (Node liquid : liquids) {
			liquid.draw(batch);
		}
	}

	public TiledMap getTiledMap() {
		return tiledMap;
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

}
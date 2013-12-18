/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.client.display;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.projecthawkthorne.client.Direction;
import com.projecthawkthorne.client.HawkthorneGame;

/**
 * 
 * @author Patrick
 */
public class Node {

	public final static String ONE = "!";
	public final static String NULL = "?";
	public static final String IMAGES_FOLDER = "data/images/";
	public static final String MAPS_FOLDER = "data/maps/";
	public static final String MAPS_OUTPUT_FOLDER = "data/packs/";
	private float x;
	private float y;
	public float width;
	public float height;
	protected String state = "default";
	private String type;
	protected String name;
	// !!!!!Warning if you change level from type Level to type String
	// this will be structurally different from the back end Node
	public String levelName;
	public int position; // frame position

	// protected SpriteBatch batch = new SpriteBatch();
	protected Texture objectTexture = null;// = new
	// Texture(Gdx.files.internal(IMAGES_FOLDER
	// + "defaultObject.png"));
	// private BitmapFont font;// = new BitmapFont();
	protected HashMap<String, String> properties;
	private Texture bboxTexture;// = new
	// Texture(Gdx.files.internal(IMAGES_FOLDER +
	// "boundingBox.png"));
	protected int srcX = 0;
	protected int srcY = 0;
	protected int srcWidth = 10;
	protected int srcHeight = 10;
	public String id;
	protected Direction direction;
	private final long creationTime = System.currentTimeMillis();
	private long lastUpdate = System.currentTimeMillis();
	private String sheetPath;
	public float[] vertices = null;
	private ShapeRenderer shapeRenderer = null;

	/**
	 * @param type
	 * @param name
	 */
	public Node(String type, String name) {
		this.name = name;
		this.setType(type);
	}

	/**
	 * draws the node if the type,name, and state are available
	 * 
	 * @param batch
	 */
	public void draw(SpriteBatch batch) {
		Animation anim;
		try {
			if (this instanceof Player) {
				Player player = (Player) this;
				anim = Assets.characters.get(player.name)
						.get(player.character.costume).get(player.getState());
			} else {
				anim = Assets.nodes.get(this.getType()).get(this.name)
						.get(this.getState());
			}
			long nowTime = Node.getCurrentTime();
			long thenTime = this.getCreationTime();
			float stateTime = Node.convertToSeconds(nowTime - thenTime);
			TextureRegion tr = anim.getKeyFrame(stateTime);

			if (this.direction == Direction.LEFT) {
				batch.draw(tr, this.x, this.y + tr.getRegionHeight(),
						tr.getRegionWidth(), -tr.getRegionHeight());
			} else {
				batch.draw(tr, this.x + tr.getRegionWidth(),
						this.getY() + tr.getRegionHeight(),
						-tr.getRegionWidth(), -tr.getRegionHeight());
			}
		} catch (NullPointerException e) {
			if (HawkthorneGame.DEBUG) {
				System.err.println(this.id);
				System.err.println(this.getType());
				System.err.println(this.name);
				if (this instanceof Player) {
					Player player = (Player) this;
					System.err.println("> " + player.character.costume);
				}
				System.err.println(this.getState());
				System.err.println();

				TextureRegion defaultTexture = Assets.standard.get("node")
						.getKeyFrame(0);
				int height = Math.round(this.height);
				height = height > 0 ? height : defaultTexture.getRegionHeight();
				int width = Math.round(this.width);
				width = width > 0 ? width : defaultTexture.getRegionWidth();

				if (this.direction == Direction.LEFT) {
					batch.draw(defaultTexture, this.x, this.y + height, width,
							-height);
				} else {
					batch.draw(defaultTexture, this.x + width, this.y + height,
							-width, -height);
				}
			}
		}
		if (this instanceof Player) {
			Player player = (Player) this;
			batch.draw(player.getIndicator(), x + player.width / 2
					- player.getIndicator().getWidth() / 2, this.y - 10);
			// TODO:instantiate the font once
			BitmapFont font = new BitmapFont(true);

			// write object details for debugging
			String tmp = player.username;
			font.drawMultiLine(batch, tmp, this.x, this.y + 30);
		}
		// TODO:instantiate the font once
		// BitmapFont font = new BitmapFont(true);

		// write object details for debugging
		// String tmp = this.getClass().getSimpleName() + "\n"
		// + this.id + "\n"
		// + this.name + "\n"
		// + this.type;
		// font.drawMultiLine(batch, tmp, this.x, this.y+30);
	}

	public void drawAsSprite(SpriteBatch batch) {
		assert (this.sheetPath != null);
		Texture t = Assets.spriteCache.get(this.sheetPath);
		if (t == null) {
			t = Assets.spriteCache.put(this.sheetPath,
					Assets.loadTexture("data/" + this.sheetPath));
		}

		if (t == null) {
			System.err.println("couldn't draw sprite");
			return;
		}
		if (this.direction == Direction.LEFT) {
			batch.draw(t, this.x, this.y + height, width, -height);
		} else {
			batch.draw(t, this.x + width, this.y + height, -width, -height);
		}
	}

	private static float convertToSeconds(long ms) {
		return ms / 1000.0f;
	}

	private long getCreationTime() {
		return creationTime;
	}

	/**
	 * current time since Jan 1, 1970 in milliseconds
	 * 
	 * @return
	 */
	private static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static Node unpack(HashMap<String, HashMap<String, Node>> world,
			String params) {
		String levelName = null;
		String id = null;
		float x = 0;
		float y = 0;
		String state = "default";
		Direction direction = Direction.RIGHT;
		int position = -1;
		String name = null;
		String type = null;
		float width = -1;
		float height = -1;
		String spritePath = null;
		String sheetPath = null;
		String polyline = null;

		String[] nodeArgs = params.split("[" + NULL + "]");
		for (int i = 0; i < nodeArgs.length; i++) {
			String[] chunks = nodeArgs[i].split(ONE);
			if (chunks.length != 3) {
				throw new UnsupportedOperationException(
						"incorrect argument amount");
			}
			String argType = chunks[0];
			String argName = chunks[1];
			String argValue = chunks[2];

			if (argName.equals("level")) {
				levelName = argValue;
			} else if (argName.equals("id")) {
				id = argValue;
			} else if (argName.equals("x")) {
				x = Float.parseFloat(argValue);
			} else if (argName.equals("y")) {
				y = Float.parseFloat(argValue);
			} else if (argName.equals("state")) {
				state = argValue;
			} else if (argName.equals("direction")) {
				try {
					direction = Direction.valueOf(argValue.toUpperCase());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (argName.equals("position")) {
				position = Integer.parseInt(argValue);
			} else if (argName.equals("name")) {
				name = argValue;
			} else if (argName.equals("type")) {
				type = argValue;
			} else if (argName.equals("width")) {
				width = Float.parseFloat(argValue);
			} else if (argName.equals("height")) {
				height = Float.parseFloat(argValue);
			} else if (argName.equals("spritePath")) {
				spritePath = argValue;
			} else if (argName.equals("sheetPath")) {
				sheetPath = argValue;
			} else if (argName.equals("poly")) {
				polyline = argValue;
			} else {
				throw new UnsupportedOperationException("argName==" + argName
						+ ", argValue==" + argValue);
			}

		}

		HashMap<String, Node> levelObjs = world.get(levelName);
		Node n = null;
		System.err.println(levelName);
		System.err.println(id + "," + type);
		// TODO, should make all nodes in one map, not per level
		if (levelObjs == null) {
			Iterator<HashMap<String, Node>> levelIt = world.values().iterator();
			// Note: I'm not a fan of labelled breaks and this code is temporary
			boolean found = false;
			while (levelIt.hasNext()) {
				HashMap<String, Node> t = levelIt.next();
				Iterator<Node> nodesIt = t.values().iterator();
				while (nodesIt.hasNext()) {
					Node node = nodesIt.next();
					if (node.id.equals(id)) {
						n = node;
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		} else {
			n = levelObjs.get(id);
		}

		if (n == null) {
			n = new Node(type, name);
			n.id = id;
			levelObjs.put(id, n);
		}

		n.levelName = levelName == null ? n.levelName : levelName;
		n.setX(x);
		n.setY(y);
		n.state = state;
		n.direction = direction;
		n.position = position <= 0 ? n.position : position;
		n.name = name == null ? n.name : name;
		n.type = type == null ? n.type : type;
		n.width = width <= 0 ? n.width : width;
		n.height = height <= 0 ? n.height : height;
		n.setSheetPath(sheetPath != null ? sheetPath : spritePath);
		n.resetUpdateTime();
		if (polyline != null) {
			polyline = polyline.trim();
			String[] coords = polyline.split("\\s+");
			n.vertices = new float[coords.length * 2];
			for (int i = 0; i < coords.length; i++) {
				String[] pair = coords[i].split(",");
				n.vertices[i * 2] = Float.parseFloat(pair[0]);
				n.vertices[i * 2 + 1] = Float.parseFloat(pair[1]);
			}
		}

		return n;
	}

	private void resetUpdateTime() {
		this.lastUpdate = System.currentTimeMillis();
	}

	public long getLastUpdate() {
		return this.lastUpdate;
	}

	public String getState() {
		return state;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSheetPath() {
		return sheetPath;
	}

	public void setSheetPath(String sheetPath) {
		this.sheetPath = sheetPath;
	}

	public void drawAsPolygon(OrthographicCamera cam) {
		assert (this.vertices != null);
		if (shapeRenderer == null) {
			shapeRenderer = new ShapeRenderer();
		}
		shapeRenderer.setProjectionMatrix(cam.combined);

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1, 0, 0, 1);
		float[] v = this.vertices;
		int n = v.length;
		for (int i = 0; i < v.length; i += 2) {
			shapeRenderer.line(v[i % n], v[(i + 1) % n] - this.height,
					v[(i + 2) % n], v[(i + 3) % n] - this.height);
		}
		shapeRenderer.end();
		shapeRenderer.end();

	}

}

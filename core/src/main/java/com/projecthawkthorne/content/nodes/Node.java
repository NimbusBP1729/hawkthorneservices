/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content.nodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.projecthawkthorne.client.display.Animation;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Direction;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.hardoncollider.Bound;
import com.projecthawkthorne.hardoncollider.Collidable;
import com.projecthawkthorne.hardoncollider.Collider;

/**
 * 
 * @author Patrick
 */
public abstract class Node extends Collidable {
	// declare behaviors
	// implement them in Enemy.updateVelocity
	// inherit them in the subclass

	public float x;
	public float y;
	public float velocityX = 0;
	public float velocityY = 0;
	public Direction direction = Direction.RIGHT;
	public float width;
	public float height;
	protected State state = State.DEFAULT;
	/**
	 * the name of the node<br>
	 * can be considered a subtype
	 */
	public String name;
	/**
	 * determines how much a node opposes gravity <br>
	 * lift = 0 implies normal gravity <br>
	 * lift = -Game.gravity implies antigravity
	 */
	protected float lift = 0;
	/**
	 * the level that the node resides in <br>
	 * Note: players can have an arbitrary Gamestate. other nodes should have a
	 * Level
	 */
	protected Gamestate level;
	/** list of the players this node is touching */
	protected Set<Player> playersTouched;
	/** position within a sequence of animations of this node */
	protected int position = 1;
	/** true if the node is dead */
	protected boolean dead;
	/** the unique id of the node */
	protected final UUID id;
	/**
	 * count of the amount of objects that have been created <br>
	 * this is used to generate ids
	 */
	private static int objectCount = 0;
	/** delimits the end of a field in the packed node representation */
	protected final static String ONE = "!";
	/** delimits the end of a parameter in the packed node representation */
	protected final static String NULL = "?";
	/** the width of a single tile */
	public static final float TILE_WIDTH = 24.0f;
	/** the height of a single tile */
	public static final float TILE_HEIGHT = 24.0f;
	/** the height of the bounding box */
	protected float bbox_height = -1;
	/** the width of the bounding box */
	protected float bbox_width = -1;
	/** the x offset of the bounding box from the node's top left position */
	protected float bboxOffsetX = 0;
	/** the y offset of the bounding box from the node's top left position */
	protected float bboxOffsetY = 0;
	/** the bounding box used by this node */
	protected Bound bb;
	// protected SpriteBatch batch = new SpriteBatch();
	// protected Texture objectTexture;// = new
	// Texture(Gdx.files.internal(IMAGES_FOLDER
	// + "defaultObject.png"));
	// private BitmapFont font;// = new BitmapFont();
	/** properties of the node initializable in the tmx file */
	protected MapProperties properties;
	/** the collider associated with this object's level */
	private Collider collider;
	/** the tiled object this object uses */
	private MapObject obj;
	/**
	 * each client will have their own if this is final
	 */
	private final long creationTime = System.currentTimeMillis();
	private String type;

	// private Texture bboxTexture;// = new
	// Texture(Gdx.files.internal(IMAGES_FOLDER + "boundingBox.png"));

	/**
	 * returns a node's bounding box
	 * 
	 * @return
	 */
	public final Bound getBb() {
		return bb;
	}

	/**
	 * assigns a bounding box to a node
	 * 
	 * @param bb
	 */
	public final void setBb(Bound bb) {
		this.bb = bb;
		this.bb.setUserData(this);
	}

	/**
	 * 
	 * @param t
	 *            tiled object that represent this node
	 * @param level
	 *            the level this node will reside in
	 */
	public Node(MapObject t, Level level) {
		this(t, level, UUID.randomUUID());
	}

	/**
	 * 
	 * @param t
	 *            tiled object that represent this node
	 * @param level
	 *            the level this node will reside in
	 */
	public Node(MapObject t, Level level, UUID id) {
		this.id = id;
		this.dead = false;
		this.level = level;
		this.obj = t;

		this.playersTouched = new HashSet<Player>();

		this.name = t.getName();
		Rectangle br;
		if(t instanceof RectangleMapObject){
			br = ((RectangleMapObject) t).getRectangle();
		}else if(t instanceof PolygonMapObject){
			br = ((PolygonMapObject) t).getPolygon().getBoundingRectangle();
		}else{
			throw new UnsupportedOperationException("type ("+t.getClass()+") is not supported");
		}
		this.x = br.x;
		this.y = br.y;
		this.width = br.width;
		this.height = br.height;

		this.properties = t.getProperties();
		this.type = properties.get("type", String.class);
		if (properties.get("width") != null) {
			this.width = properties.get("width", Float.class);
		}
		if (properties.get("height") != null) {
			this.height = properties.get("height", Float.class);
		}
		this.setBound(t);

	}

	/**
	 * returns the id of the node
	 * 
	 * @return the unique id of the node
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * completely destroys the node. might be too aggressive
	 */
	public void die() {
		this.dead = true;
		this.level.getNodeMap().remove(this.id);
		Set<Player> players = this.level.getPlayers();
		Iterator<Player> pit = players.iterator();
		while (pit.hasNext()) {
			Player p = pit.next();
			p.updateList.add(this);
		}
		if (this.level instanceof Level) {
			Level lvl = (Level) this.level;
			lvl.getCollider().removeBox(bb);
		}
		this.bb = null;
	}

	/**
	 * Note: if we have a polygon we change the x and y to correspond with the
	 * top left of the polygon's bounding box
	 * 
	 * @param obj
	 */
	public void setBound(MapObject obj) {

		if (obj instanceof PolylineMapObject) {
			throw new UnsupportedOperationException(
					"polylines aren't supported yet");
		} else if (obj instanceof PolygonMapObject) {
			PolygonMapObject pObj = (PolygonMapObject) obj;
			if (this.properties.get("bbox_width") != null) {
				this.bbox_width = Float.parseFloat(this.properties.get(
						"bbox_width", String.class));
			}
			if (this.properties.get("bbox_height") != null) {
				this.bbox_height = Float.parseFloat(this.properties.get(
						"bbox_height", String.class));
			}

			if (this.bbox_width < 0 && this.properties.get("width") != null) {
				this.bbox_width = Float.parseFloat(this.properties.get("width",
						String.class));
			}
			if (this.bbox_height < 0 && this.properties.get("height") != null) {
				this.bbox_height = Float.parseFloat(this.properties.get(
						"height", String.class));
			}
			if (this.bbox_width < 0) {
				this.bbox_width = width;
			}
			if (this.bbox_height < 0) {
				this.bbox_height = height;
			}
			if (this.bbox_width < 0 || this.bbox_height < 0) {
				Gdx.app.error("Node error", "no bounds for:" + this.type + ","
						+ this.name);
			}
			this.bb = Bound.create(pObj.getPolygon().getVertices());
		} else {
			if (this.properties.get("bbox_width") != null) {
				this.bbox_width = Float.parseFloat(this.properties.get(
						"bbox_width", String.class));
			}
			if (this.properties.get("bbox_height") != null) {
				this.bbox_height = Float.parseFloat(this.properties.get(
						"bbox_height", String.class));
			}

			if (this.bbox_width < 0 && this.properties.get("width") != null) {
				this.bbox_width = Float.parseFloat(this.properties.get("width",
						String.class));
			}
			if (this.bbox_height < 0 && this.properties.get("height") != null) {
				this.bbox_height = Float.parseFloat(this.properties.get(
						"height", String.class));
			}
			if (this.bbox_width < 0) {
				this.bbox_width = width;
			}
			if (this.bbox_height < 0) {
				this.bbox_height = height;
			}
			if (this.bbox_width < 0 || this.bbox_height < 0) {
				Gdx.app.error("Node error", "no bounds for:" + this.type + ","
						+ this.name);
			}
			this.bb = Bound.create(this.x, this.y, this.bbox_width,
					this.bbox_height);
		}


		if (level instanceof Level) {
			Level lvl = (Level) this.level;
			lvl.getCollider().addBox(this.bb);
		}
		this.bb.setUserData(this);
	}

	public void setIsKeyDown(GameKeys button, boolean b) {
	};

	/**
	 * updates the node
	 * 
	 * @param dt
	 */
	public void update(long dt) {
		this.updateVelocity(dt);
		this.x = this.x + this.velocityX * dt;
		this.y = this.y + this.velocityY * dt;
		this.moveBoundingBox();
	}

	/**
	 * updates the present velocity of this node<br>
	 * positions are managed in update
	 * 
	 * @param dt
	 */
	protected abstract void updateVelocity(long dt);

	protected abstract void collide(Node node);

	/**
	 * called when this stops colliding with node
	 * 
	 * @param node
	 *            the node that stopped colliding
	 */
	protected abstract void collideEnd(Node node);

	/**
	 * @return the level
	 */
	public Gamestate getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level you want the node to reside in
	 */
	public void setLevel(Gamestate level) {
		this.level = level;
	}

	/**
	 * returns true if someone can pickup this node
	 * 
	 * @return true if someone can pickup this node
	 */
	boolean canPickup() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * picks up this node by a player
	 * 
	 * @param aThis
	 *            the player that is picking this node up
	 */
	public void pickup(Player aThis) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * throws this node
	 * 
	 * @param aThis
	 *            the player that is throwing this node
	 */
	public void throw_normal(Player aThis) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * 
	 * @param aThis
	 */
	public void throw_vertical(Player aThis) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * drops a node by a plyer
	 * 
	 * @param aThis
	 *            the player that dropped the node
	 */
	public void drop(Player aThis) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * returns the state of a node
	 * 
	 * @return the state of the node
	 */
	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	/**
	 * moves the bounding box to match the node's current position
	 */
	public void moveBoundingBox() {
		if (this.level instanceof Level) {
			// FIXME this shouldn't be necessary
			if (this.bb == null) {
				this.setBound(this.obj);
			}

			this.bbox_width = this.bb.getWidth();
			this.bbox_height = this.bb.getHeight();
			this.bb.setX(this.x + this.bboxOffsetX);
			this.bb.setY(this.y + this.bboxOffsetY);
		}
	}

	/**
	 * @return true if the node is dead
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * reaction by a node of a player keypress <br>
	 * the player had to be touching the node for this to be triggered
	 * 
	 * @param button
	 *            the key that was pressed
	 * @param player
	 *            the player who pressed it
	 * @return true if the node handled the keypress
	 */
	public boolean playerKeypressed(GameKeys button, Player player) {
		return false;
	}

	@Override
	public void onCollision(Collidable node) {
		super.onCollision(node);
		if (node instanceof Player && !this.playersTouched.contains(node)) {
			Player player = (Player) node;
			this.playersTouched.add(player);
		}
	}

	@Override
	public void onCollisionEnd(Collidable node) {
		super.onCollisionEnd(node);
		if (node instanceof Player) {
			this.playersTouched.remove(node);
		}
	}

	public void collision(Collidable node) {
		this.collide((Node) node);
	}

	public void collisionEnd(Collidable node) {
		this.collideEnd((Node) node);
	}

	protected final boolean propToBoolean(Object obj) {
		if (obj == null) {
			return false;
		} else {
			return Boolean.parseBoolean((String) obj);
		}
	}

	public long getDuration() {
		return System.currentTimeMillis() - creationTime;
	}

	/**
	 * draws the this if the type,name, and state are available
	 * 
	 * @param batch
	 */
	public void draw(SpriteBatch batch) {
		Animation anim = null;
		try {
			// anim = null if the type doesn't appear in the nodes map
			anim = Assets.nodeSpriteMap.lookUp(type, name, state);
			if (anim != null) {
				float stateTime = convertToSeconds(this.getDuration());
				TextureRegion tr = anim.getKeyFrame(stateTime);
				batch.draw(tr, this.x, this.y, this.width, this.height);
			}
		} catch (NullPointerException e) {
				Gdx.app.error("node draw error", "(id,type,name,state)==("
						+ this.getId() + "," 
						+ this.type + "," 
						+ this.name + ","
						+ this.getState() + ")");
		}
	}

	/**
	 * converts from millisecnds to seconds
	 * 
	 * @param ms
	 * @return
	 */
	protected static float convertToSeconds(long ms) {
		return ms / 1000.0f;
	}

}

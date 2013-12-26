/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.projecthawkthorne.client.display;

import static com.projecthawkthorne.content.Game.DEBUG;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.content.Direction;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Node;
import com.projecthawkthorne.content.nodes.State;
import com.projecthawkthorne.gamestate.elements.RadioButtonGroup;

public class Assets {
	public static final String SRC_IMAGES = "../data/images/";
	private static BitmapFont font = new BitmapFont(false);

	/** */
	public static Map<String, Texture> spriteCache = new HashMap<String, Texture>();

	/** top level map for all non-player modes */
	public static Map<String, Map<String, Map<String, Animation>>> nodes;

	private static Texture enemyTexture;
	private static Map<String, Map<String, Animation>> enemy;
	private static Map<String, Animation> acorn;
	private static Map<String, Animation> hippy;

	private static Texture materialTexture;
	private static HashMap<String, Map<String, Animation>> material;
	private static HashMap<String, Animation> leaf;
	private static HashMap<String, Animation> rock;
	private static HashMap<String, Animation> stick;

	private static Texture tokenTexture;
	private static HashMap<String, Map<String, Animation>> token;
	private static HashMap<String, Animation> coin;
	private static HashMap<String, Animation> health;

	/** top level map for all characters */
	public static Map<String, Map<String, Map<State, Animation>>> characters;
	private static Texture abedBaseTexture;
	private static Map<String, Map<State, Animation>> abed;
	private static Map<State, Animation> base;

	/** map for default sprites */
	public static HashMap<String, Animation> standard;
	private static Texture defaultTexture;
	private static Texture bboxTexture;

	public static Texture loadTexture(String file) {
		Texture t;
		try {
			t = new Texture(Gdx.files.internal(file));
		} catch (Exception e) {
			System.err.println("failed to load '" + file
					+ "': using 'defaultTexture.png'");
			t = new Texture(Gdx.files.internal("../data/images/"
					+ "defaultTexture.png"));
		}
		return t;
	}

	public static void load() {
		//
		standard = new HashMap<String, Animation>();
		defaultTexture = loadTexture(SRC_IMAGES + "defaultTexture.png");
		bboxTexture = loadTexture(SRC_IMAGES + "bboxTexture.png");

		// create blank nodes map
		nodes = new HashMap<String, Map<String, Map<String, Animation>>>();

		// create enemy list
		enemyTexture = loadTexture(SRC_IMAGES + "enemies.png");
		enemy = new HashMap<String, Map<String, Animation>>();

		// create each enemy
		// 1) make new map
		// 2) add animation states to it
		// 3) add the map to the enemy mapp
		acorn = new HashMap<String, Animation>();
		acorn.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.LOOP,
				new TextureRegion(enemyTexture, 40, 0, 20, 20),
				new TextureRegion(enemyTexture, 60, 0, 20, 20),
				new TextureRegion(enemyTexture, 80, 0, 20, 20)));
		acorn.put("attack", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.LOOP,
				new TextureRegion(enemyTexture, 140, 0, 20, 20),
				new TextureRegion(enemyTexture, 160, 0, 20, 20),
				new TextureRegion(enemyTexture, 180, 0, 20, 20)));
		enemy.put("acorn", acorn);

		hippy = new HashMap<String, Animation>();
		hippy.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.LOOP,
				new TextureRegion(enemyTexture, 94, 118, 47, 47),
				new TextureRegion(enemyTexture, 141, 118, 47, 47)));
		hippy.put("attack", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.LOOP,
				new TextureRegion(enemyTexture, 0, 118, 47, 47),
				new TextureRegion(enemyTexture, 47, 118, 47, 47)));
		hippy.put("dying", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(enemyTexture, 192, 118, 47, 47)));
		enemy.put("hippy", hippy);

		materialTexture = loadTexture(SRC_IMAGES + "materials.png");
		material = new HashMap<String, Map<String, Animation>>();

		leaf = new HashMap<String, Animation>();
		leaf.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(materialTexture, 0, 0, 24, 24)));
		material.put("leaf", leaf);
		rock = new HashMap<String, Animation>();
		rock.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(materialTexture, 24, 0, 24, 24)));
		material.put("rock", rock);
		stick = new HashMap<String, Animation>();
		stick.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(materialTexture, 48, 0, 24, 24)));
		material.put("stick", stick);

		tokenTexture = loadTexture(SRC_IMAGES + "tokens.png");
		token = new HashMap<String, Map<String, Animation>>();
		health = new HashMap<String, Animation>();
		health.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(materialTexture, 0, 0, 8, 8),
				new TextureRegion(materialTexture, 8, 0, 8, 8)));
		token.put("health", health);
		coin = new HashMap<String, Animation>();
		coin.put("default", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(tokenTexture, 0, 9, 13, 10),
				new TextureRegion(tokenTexture, 13, 9, 13, 10)));
		token.put("coin", coin);

		// add each node type to the node list
		nodes.put("enemy", enemy);
		nodes.put("material", material);
		nodes.put("token", token);

		abedBaseTexture = loadTexture(SRC_IMAGES + "characters/abed/base.png");

		characters = new HashMap<String, Map<String, Map<State, Animation>>>();
		abed = new HashMap<String, Map<State, Animation>>();
		base = new HashMap<State, Animation>();
		base.put(State.IDLE, new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(abedBaseTexture, 0, 0, 48, 48)));
		base.put(State.WALK, new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.LOOP,
				new TextureRegion(abedBaseTexture, 48, 0, 48, 48),
				new TextureRegion(abedBaseTexture, 96, 0, 48, 48),
				new TextureRegion(abedBaseTexture, 144, 0, 48, 48)));
		base.put(State.JUMP, new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(abedBaseTexture, 288, 0, 48, 48)));
		base.put(State.CROUCH, new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(abedBaseTexture, 8 * 48, 2 * 48, 48, 48)));
		base.put(State.GAZE, new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(abedBaseTexture, 7 * 48, 0 * 48, 48, 48)));
		abed.put("base", base);
		characters.put("abed", abed);

		standard.put("node", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(defaultTexture, 288, 0, 48, 48)));
		standard.put("bbox", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(bboxTexture, 288, 0, 48, 48)));
	}

	public static void playSound(Sound sound) {
		sound.play(1);
	}

	public static void draw(SpriteBatch batch, RadioButtonGroup elem) {
		Texture rbgTexture = loadTexture(Assets.SRC_IMAGES
				+ "defaultTexture.png");

		batch.draw(rbgTexture, elem.getX(), elem.getY(), elem.getWidth(),
				elem.getHeight());
		float x = elem.getX();
		float y = elem.getY();

		for (String ro : elem.getOptions()) {
			if (ro.equals(elem.getOptions()[elem.getSelection()])) {
				batch.setColor(0, 1, 0, 1);
			} else if (ro.equals(elem.getOptions()[elem.getCursor()])) {
				batch.setColor(1, 0, 0, 1);
			} else {
				batch.setColor(1, 1, 1, 1);
			}
			font.drawMultiLine(batch, ro, x, y);
			y -= 10;
		}

	}

	/**
	 * draws the node if the type,name, and state are available
	 * 
	 * @param batch
	 */
	public static void draw(SpriteBatch batch, Node node) {
		Animation anim;
		try {
			if (node instanceof Player) {
				Player player = (Player) node;
				anim = Assets.characters.get(player.getCharacter().getName())
						.get(player.getCharacter().getCostume())
						.get(player.getState());
			} else {
				try {
					anim = Assets.nodes
							.get(node.getClass().getSimpleName().toLowerCase())
							.get(node.name).get(node.getState());
				} catch (NullPointerException e) {
					anim = null;
				}
			}

			// only draw nodes that have an associated image
			if (anim != null) {
				float stateTime = convertToSeconds(node.getDuration());
				TextureRegion tr = anim.getKeyFrame(stateTime);

				if (node.direction == Direction.LEFT) {
					batch.draw(tr, node.x, node.y, tr.getRegionWidth(),
							tr.getRegionHeight());
				} else {
					batch.draw(tr, node.x + tr.getRegionWidth(), node.y,
							-tr.getRegionWidth(), tr.getRegionHeight());
				}
			}

			if (DEBUG) {
				TextureRegion bboxTextureRegion = Assets.standard.get("bbox")
						.getKeyFrame(0);
				batch.draw(bboxTextureRegion, node.getBb().getX(), node.getBb()
						.getY() + node.getBb().getHeight(), node.getBb()
						.getWidth(), -node.getBb().getHeight());
			}
		} catch (NullPointerException e) {
			if (DEBUG) {
				System.err.println(node.getId());
				System.err.println(node.getClass().getSimpleName());
				System.err.println(node.name);
				if (node instanceof Player) {
					Player player = (Player) node;
					System.err.println("> "
							+ player.getCharacter().getCostume());
				}
				System.err.println(node.getState());
				System.err.println();

				TextureRegion defaultTextureRegion = Assets.standard
						.get("node").getKeyFrame(0);
				int height = Math.round(node.height);
				height = height > 0 ? height : defaultTextureRegion
						.getRegionHeight();
				int width = Math.round(node.width);
				width = width > 0 ? width : defaultTextureRegion
						.getRegionWidth();

				if (node.direction == Direction.LEFT) {
					batch.draw(defaultTextureRegion, node.x, node.y + height,
							width, -height);
				} else {
					batch.draw(defaultTextureRegion, node.x + width, node.y
							+ height, -width, -height);
				}

			}
		}
		if (node instanceof Player) {
			Player player = (Player) node;
			font.drawMultiLine(batch, player.getCharacter().getName(), node.x,
					node.y + 30);
		}
	}

	/**
	 * converts from millisecnds to seconds
	 * 
	 * @param ms
	 * @return
	 */
	private static float convertToSeconds(long ms) {
		return ms / 1000.0f;
	}

}

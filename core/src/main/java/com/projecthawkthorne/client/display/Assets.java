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

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static final String SRC_IMAGES = "data/maps/images/";
	public static final BitmapFont font = new BitmapFont(false);

	/** */
	public static Map<String, Texture> spriteCache = new HashMap<String, Texture>();

	/** top level map for all characterSpriteMap */
	public static CharacterSpriteMap characterSpriteMap = new CharacterSpriteMap();
	public static NodeSpriteMap nodeSpriteMap = new NodeSpriteMap();

	/** map for default sprites */
	public static HashMap<String, Animation> standard;
	private static Texture defaultTexture;
	private static Texture bboxTexture;

	private static Map<String, Texture> imageCache = new HashMap<String, Texture>();

	public static Texture loadTexture(String file) {
		Texture t;
		try {
			t = imageCache.get(file);
			if (t == null) {
				t = new Texture(Gdx.files.internal(file));
				imageCache.put(file, t);
			}
		} catch (Exception e) {
			Gdx.app.error("image cache error", "failed to load '" + file
					+ "': using 'defaultTexture.png'", e);
			t = new Texture(Gdx.files.internal(SRC_IMAGES
					+ "defaultTexture.png"));
			imageCache.put(file, t);
		}
		return t;
	}

	public static void load() {
		//
		standard = new HashMap<String, Animation>();
		defaultTexture = loadTexture(SRC_IMAGES + "defaultTexture.png");
		bboxTexture = loadTexture(SRC_IMAGES + "bboxTexture.png");

		standard.put("node", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(defaultTexture, 288, 0, 48, 48)));
		standard.put("bbox", new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(bboxTexture, 288, 0, 48, 48)));
	}
}

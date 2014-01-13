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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static final String SRC_IMAGES = "data/maps/images/";
	private static final String SRC_AUDIO = "data/audio/";
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
	public static AssetManager manager = new AssetManager();
	private static Map<String,Texture> textureCache = new HashMap<String,Texture>();
	private static Map<String, Music> musicCache = new HashMap<String,Music>();
	private static Map<String, Music> sfxCache = new HashMap<String,Music>();

	public static Texture loadTexture(String file) {
		Texture t = textureCache.get(file);
		if(t==null){
			manager.load(file, Texture.class);
			manager.finishLoading();
			t = manager.get(file, Texture.class);
			textureCache.put(file,t);
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
	
	
	public static void playSfx(String soundFile) {
		String fullName = SRC_AUDIO + "sfx/" + soundFile;
		Music music = sfxCache.get(fullName);
		if(music==null){
			manager.load(fullName, Music.class);
			manager.finishLoading();
			music = manager.get(fullName, Music.class);
			sfxCache.put(fullName,music);
		}
		music.setVolume(0.13f);
		music.setLooping(false);
		music.play();
	}

	public static void stopMusic(String soundFile) {
		Music music = manager.get(SRC_AUDIO+"music/"+soundFile+".ogg", Music.class);
		music.stop();
	}

	public static void playMusic(String soundFile) {
		playMusic(soundFile, 0.15f);
	}

	public static void playMusic(String soundFile, float volume) {
		if(soundFile==null){
			playMusic("level",volume);
			return;
		}		
		for(Music m : musicCache.values()){
			m.stop();
		}
		String fullName = SRC_AUDIO+"music/"+soundFile+".ogg";
		Music music = musicCache.get(fullName);
		if(music==null){
			manager.load(fullName, Music.class);
			manager.finishLoading();
			music = manager.get(fullName, Music.class);
			musicCache.put(fullName, music);
		}
		music.setVolume(volume);
		music.setLooping(true);
		music.play();
	}

	/**
	 * removes music from the cache
	 * 
	 * @param soundFile
	 * @return true if the soundFile was previously cached
	 */
	public static boolean removeMusic(String soundFile) {
		Music music = manager.get(SRC_AUDIO+"music/"+soundFile, Music.class);
		if(music==null)
			return false;
		music.dispose();
		return true;
	}
}

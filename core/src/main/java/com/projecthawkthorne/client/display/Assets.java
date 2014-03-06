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
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	private static final String SRC_IMAGES = "data/images/";
	private static final String SRC_SPRITE_IMAGES = "data/";
	private static final String SRC_AUDIO = "data/audio/";
	private static final String SRC_MAPS = "data/maps/";
	
	/** */
	public static Map<String, Texture> spriteCache = new HashMap<String, Texture>();

	/** top level map for all characterSpriteMap */
	public static CharacterSpriteMap characterSpriteMap = new CharacterSpriteMap();
	public static NodeSpriteMap nodeSpriteMap = new NodeSpriteMap();

	private static Texture defaultTexture;
	public static Texture bboxTexture;
	private static AssetManager manager;
	public static Texture loadTextureOrSprite(String fullName) {
		TextureParameter param = new TextureParameter();
		param.magFilter = TextureFilter.Nearest;
		param.minFilter = TextureFilter.Nearest;
		manager.load(fullName, Texture.class, param);
		manager.finishLoading();
		return manager.get(fullName, Texture.class);
	}
	
	public static Texture loadTexture(String textureFile) {
		String fullName = SRC_IMAGES + textureFile;
		return loadTextureOrSprite(fullName);
	}
	
	public static Texture loadSprite(String spriteFile) {
		String fullName = SRC_SPRITE_IMAGES + spriteFile;
		return loadTextureOrSprite(fullName);
	}
	
	public static void load(AssetManager assetManager) {
		manager = assetManager;
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		defaultTexture = loadTexture("defaultTexture.png");
		bboxTexture = loadTexture("bboxTexture.png");
	}

	public static void playSfx(String soundFile, boolean looping) {
		String fullName = SRC_AUDIO + "sfx/" + soundFile + ".ogg";
		manager.load(fullName, Music.class);
		manager.finishLoading();
		Music music = manager.get(fullName, Music.class);
		music.setVolume(0.13f);
		music.setLooping(looping);
		music.play();
	}
	
	public static void playSfx(String soundFile) {
		playSfx(soundFile,false);
	}
	
	public static void stopSfx(String soundFile) {
		String fullName = SRC_AUDIO + "sfx/" + soundFile + ".ogg";
		manager.load(fullName, Music.class);
		manager.finishLoading();
		Music music = manager.get(fullName, Music.class);
		music.stop();
	}

	public static void stopMusic(String soundFile) {
		if(soundFile!=null){
			Music music = manager.get(SRC_AUDIO+"music/"+soundFile+".ogg", Music.class);
			music.stop();
		}
	}

	public static void playMusic(String soundFile) {
		playMusic(soundFile, 0.15f);
	}

	public static void playMusic(String soundFile, float volume) {
		if(soundFile==null){
			playMusic("level",volume);
			return;
		}
		String fullName = SRC_AUDIO+"music/"+soundFile+".ogg";
		Music music;
		manager.load(fullName, Music.class);
		manager.finishLoading();
		music = manager.get(fullName, Music.class);
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
	
	public static BitmapFont getFont(){
		String fullName = "fonts/font.fnt";
		manager.load(fullName, BitmapFont.class);
		manager.finishLoading();
		return manager.get(fullName, BitmapFont.class);
	}
	
	public static TiledMap getTiledMap(String mapName){
		String fullName = SRC_MAPS + mapName + ".tmx";
		manager.load(fullName, TiledMap.class);
		manager.finishLoading();
		return manager.get(fullName, TiledMap.class);
	}
	
}

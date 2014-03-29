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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.projecthawkthorne.client.CharacterBundle;

public class Assets {
	private static final String SRC_IMAGES = "data/images/";
	private static final String SRC_SPRITE_IMAGES = "data/";
	private static final String SRC_AUDIO = "data/audio/";
	private static final String SRC_MAPS = "data/maps/";
	private static final String SRC_CHARACTERS = "data/images/characters/";
	
	/** */
	public static Map<String, Texture> spriteCache = new HashMap<String, Texture>();

	/** top level map for all characterSpriteMap */
	public static CharacterSpriteMap characterSpriteMap = new CharacterSpriteMap();
	public static NodeSpriteMap nodeSpriteMap = new NodeSpriteMap();

	private static Texture defaultTexture;
	public static Texture bboxTexture;
	private static AssetManager manager;
	
	public static void load() {
		manager = new AssetManager();
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		defaultTexture = loadTexture("defaultTexture.png");
		bboxTexture = loadTexture("bboxTexture.png");
	}
	
	public static Texture loadTextureOrSprite(String fullName) {
		TextureParameter param = new TextureParameter();
		param.magFilter = TextureFilter.Nearest;
		param.minFilter = TextureFilter.Nearest;
		manager.load(fullName, Texture.class, param);
		manager.finishLoading();
		Texture t = manager.get(fullName, Texture.class);
		if(!MathUtils.isPowerOfTwo(t.getWidth())
				|| !MathUtils.isPowerOfTwo(t.getHeight()) ){
			System.err.println(fullName +" not power of two");
			System.err.println(t.getWidth()+"x"+t.getHeight());
		}
		return t;
	}
	
	public static Texture loadTexture(String textureFile) {
		String fullName = SRC_IMAGES + textureFile;
		return loadTextureOrSprite(fullName);
	}
	
	public static Texture loadSprite(String spriteFile) {
		String fullName = SRC_SPRITE_IMAGES + spriteFile;
		return loadTextureOrSprite(fullName);
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
		try{
			if(soundFile!=null){
				Music music = manager.get(SRC_AUDIO+"music/"+soundFile+".ogg", Music.class);
				music.stop();
			}
		}catch(Exception e){
			Gdx.app.error("music", "soundFile:"+soundFile+" loading error");
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
		try{
			String fullName = SRC_AUDIO+"music/"+soundFile+".ogg";
			Music music;
			manager.load(fullName, Music.class);
			manager.finishLoading();
			music = manager.get(fullName, Music.class);
			music.setVolume(volume);
			music.setLooping(true);
			music.play();
		}catch(Exception e){
			Gdx.app.error("music", "soundFile:"+soundFile+" loading error");
		}
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
	public static BitmapFont getFont(String file) {
		String fullName = "fonts/"+file+".fnt";
		BitmapFontParameter param = new BitmapFontParameter();
		param.magFilter = TextureFilter.Nearest;
		param.minFilter = TextureFilter.Nearest;
		manager.load(fullName, BitmapFont.class, param);
		manager.finishLoading();
		return manager.get(fullName, BitmapFont.class);
	}
	
	public static BitmapFont getFont(){
		return getFont("font");
	}	
	
	public static TiledMap getTiledMap(String mapName){
		String fullName = SRC_MAPS + mapName + ".tmx";
		manager.load(fullName, TiledMap.class);
		manager.finishLoading();
		return manager.get(fullName, TiledMap.class);
	}

	public static TextureRegion loadTextureRegion(String fullName, int x, int y, int width, int height) {
		TextureParameter param = new TextureParameter();
		param.magFilter = TextureFilter.Nearest;
		param.minFilter = TextureFilter.Nearest;
		manager.load(fullName, Texture.class, param);
		manager.finishLoading();
		TextureRegion texture = new TextureRegion(manager.get(fullName, Texture.class), x, y, width, height);
		Texture t = texture.getTexture();
		if(!MathUtils.isPowerOfTwo(t.getWidth())
				|| !MathUtils.isPowerOfTwo(t.getHeight()) ){
			System.err.println(fullName +" not power of two");
			System.err.println(t.getWidth()+"x"+t.getHeight());
		}
		return texture;
	}

	public static void dispose() {
		manager.dispose();
	}

	public static CharacterBundle  getCharacter(String name) {
		String fullName = SRC_CHARACTERS+name+"/base.png";
		TextureRegion texture = Assets.loadTextureRegion(fullName, 0, 48, 48, -48);
		CharacterBundle bundle = new CharacterBundle();
		bundle.setName(name);
		bundle.setTexture(texture);
		return bundle;
	}

}

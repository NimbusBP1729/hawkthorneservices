package com.projecthawkthorne.client.display;

import static com.projecthawkthorne.client.display.Assets.loadTexture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.content.nodes.State;

public class CharacterSpriteMap {

	public Animation lookUp(String name, String costume, State state) {
		Animation anim = null;
		switch(state){
		case ATTACK:
			break;
		case ATTACKJUMP:
			break;
		case ATTACKWALK:
			break;
		case CROUCH:
			anim = new Animation(0.2f,
					com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 8 * 48, 2 * 48, 48, 48));
			break;
		case CROUCHWALK:
			break;
		case DEAD:
			anim = new Animation(0.2f,
					com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 8 * 48, 12 * 48, 48, 48));
			break;
		case DEFAULT:
			break;
		case DYING:
			break;
		case DYINGATTACK:
			break;
		case GAZE:
			anim = new Animation(0.2f,
					com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 7 * 48, 0 * 48, 48, 48));
			break;
		case GAZEIDLE:
			anim = new Animation(0.2f,
					com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 0 * 48, 3 * 48, 48, 48));
			break;
		case GAZEWALK:
			anim = new Animation(0.16f,
					com.badlogic.gdx.graphics.g2d.Animation.LOOP,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 1 * 48, 3 * 48, 48, 48),
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 2 * 48, 3 * 48, 48, 48));
			break;
		case HOLD:
			break;
		case HOLDJUMP:
			break;
		case HOLDWALK:
			break;
		case HURT:
			break;
		case IDLE:
			anim = new Animation(0.2f
					,com.badlogic.gdx.graphics.g2d.Animation.NORMAL
					,new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 0, 0, 48, 48));
			break;
		case JUMP:
			anim = new Animation(0.2f,
				com.badlogic.gdx.graphics.g2d.Animation.NORMAL,
				new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 288, 0, 48, 48));
			break;
		case WALK:
			anim = new Animation(0.2f,
					com.badlogic.gdx.graphics.g2d.Animation.LOOP,
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 48, 0, 48, 48),
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 96, 0, 48, 48),
					new TextureRegion(loadTexture("characters/"+name+"/"+costume+".png"), 144, 0, 48, 48));
			break;
		case WIELDIDLE:
			break;
		case WIELDJUMP:
			break;
		case WIELDWALK:
			break;
		default:
			break;
		}
		return anim;
	}

}

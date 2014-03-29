package com.projecthawkthorne.client;

import static com.projecthawkthorne.client.HawkthorneGame.HEIGHT;
import static com.projecthawkthorne.client.HawkthorneGame.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;

public class HawkthorneUserInterface implements InputProcessor{
	private Texture uidirectional = Assets.loadTexture("ui/uidirectional.png");
	private Texture uidirectionalsmall = Assets.loadTexture("ui/uidirectionalsmall.png");
	private float startX;
	private float startY;
	private float currentX;
	private float currentY;
	private boolean dragging;
	private OrthographicCamera cam;
	private SpriteBatch batch = new SpriteBatch();
	private boolean IS_Y_DOWN = true;
	private int dragPointer = -1;
	private Vector3 vec3 = new Vector3(0,0,0);
	private BitmapFont font;
	private float jumpX;
	private float jumpY;
	
	
	public HawkthorneUserInterface(){
		cam = new OrthographicCamera(WIDTH/2, HEIGHT/2);
		cam.setToOrtho(IS_Y_DOWN);
		cam.zoom = 0.5f;
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		font = Assets.getFont();
	}

	public void draw() {
		cam.zoom = 1f;
		batch.setProjectionMatrix(cam.combined);
		cam.update();
		
		batch.begin();
		if(dragging){
			batch.setColor(1, 1, 1, 0.5f);
			batch.draw(uidirectional, startX - uidirectional.getWidth()/2, startY - uidirectional.getHeight()/2);
			float diffX = currentX - startX;
			float diffY = currentY - startY;
			float length = (float) Math.sqrt(diffX*diffX+diffY*diffY);	
			int padW = uidirectionalsmall.getWidth()/2;
			int padH = uidirectionalsmall.getHeight()/2;
			float widgetX = startX + MathUtils.round((uidirectional.getWidth()/2-padW)*diffX/length); 
			float widgetY = startY + 	MathUtils.round((uidirectional.getHeight()/2-padH)*diffY/length);
			batch.draw(uidirectionalsmall, widgetX - uidirectionalsmall.getWidth()/2, widgetY - uidirectionalsmall.getHeight()/2);
		}


		batch.setColor(1, 0, 0, 0.5f);
		if(getIsAndroidKeyDown(GameKeys.DOWN)){
			batch.draw(uidirectionalsmall
					, startX - uidirectionalsmall.getWidth()/2 
					, startY - uidirectionalsmall.getHeight()/2+uidirectional.getHeight()/2- uidirectionalsmall.getHeight()/2);
		}
		if(getIsAndroidKeyDown(GameKeys.UP)){
			batch.draw(uidirectionalsmall
					, startX - uidirectionalsmall.getWidth()/2 
					, startY - uidirectionalsmall.getHeight()/2-uidirectional.getHeight()/2+ uidirectionalsmall.getHeight()/2);
		}
		if(getIsAndroidKeyDown(GameKeys.RIGHT)){
			batch.draw(uidirectionalsmall
					, startX - uidirectionalsmall.getWidth()/2 +uidirectional.getWidth()/2- uidirectionalsmall.getWidth()/2
					, startY - uidirectionalsmall.getHeight()/2);
		}
		if(getIsAndroidKeyDown(GameKeys.LEFT)){
			batch.draw(uidirectionalsmall
					, startX - uidirectionalsmall.getWidth()/2 - uidirectional.getWidth()/2 + uidirectionalsmall.getWidth()/2
					, startY - uidirectionalsmall.getHeight()/2);
		}
		
		if(getIsAndroidKeyDown(GameKeys.JUMP)){
			font.setColor(1, 0, 0, 0.5f);
			font.draw(batch, "JUMP", jumpX, jumpY);
			font.setColor(Color.WHITE);
		}
		

		batch.setColor(Color.WHITE);
		batch.end();
	}

	public void resize(int width, int height) {
	}

	public void dispose() {
	}

	public void update(long dt) {
	}

	public boolean getIsAndroidKeyDown(GameKeys gk) {

		boolean result = false;
		
		if(gk == GameKeys.START && Gdx.input.isKeyPressed(Keys.BACK)){
			result = true;
		}
		
		if(dragging){
			int min = 5;
			if(currentX - startX > min && gk == GameKeys.RIGHT){
				result = true;
			} else if(currentX - startX < -min && gk == GameKeys.LEFT){
				result = true;
			} else if(currentY - startY > min && gk == GameKeys.DOWN){
				result = true;
			} else if(currentY - startY < -min && gk == GameKeys.UP){
				result = true;
			}
		}

		for (int i = 0; i < 20; i++) {
			if (!Gdx.input.isTouched(i)) {
				continue;
			}
			if(dragging && i == dragPointer){
				continue;
			}
			if (Gdx.input.getX(i) < Gdx.graphics.getWidth() / 2) {
			} else {
				switch (gk) {
				case JUMP:
					result = true;
					break;
				default:
					break;
				}
			}

		}
		return result;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!dragging){
			vec3.x = screenX;
			vec3.y = screenY;
			cam.unproject(vec3);
			startX = vec3.x;
			startY = vec3.y;
		}
		
		if(screenX > Gdx.graphics.getWidth() / 2){
			vec3.x = screenX;
			vec3.y = screenY;
			cam.unproject(vec3);
		
			jumpX = vec3.x;
			jumpY = vec3.y;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(dragPointer == pointer){
			dragging = false;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (screenX < Gdx.graphics.getWidth() / 2) {
			dragging = true;
			dragPointer = pointer;
		}
		if (dragPointer == pointer) {
			vec3.x = screenX;
			vec3.y = screenY;
			cam.unproject(vec3);
			
			currentX = vec3.x;
			currentY = vec3.y;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}

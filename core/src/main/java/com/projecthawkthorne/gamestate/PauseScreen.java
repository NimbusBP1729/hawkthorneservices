package com.projecthawkthorne.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.client.audio.AudioCache;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;

public class PauseScreen extends GenericGamestate {

	private BitmapFont font = new BitmapFont(true);
	private TextureRegion background = new TextureRegion(Assets.loadTexture(Assets.SRC_IMAGES+"menu/pause.png"));
	private int option = 0;
	private TextureRegion arrow = new TextureRegion(Assets.loadTexture(Assets.SRC_IMAGES+"menu/arrow.png"));
	private String musicFile = "daybreak";
	private OrthographicCamera cam = new OrthographicCamera(528, 336);


	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		AudioCache.playMusic(musicFile);
	}

	@Override
	public void hide() {
		AudioCache.stopMusic(musicFile);
	}

	@Override
	public void pause() {
		AudioCache.stopMusic(musicFile);
	}

	@Override
	public void resume() {
		AudioCache.playMusic(musicFile);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void keypressed(GameKeys gk) {
	    switch(gk){
		case DOWN:
			this.option = (this.option + 1) % 5;
            AudioCache.playSfx("click");
			break;
		case JUMP:
			AudioCache.playSfx("confirm");
			context.goBack();
			break;
		case SELECT:
			break;
		case UP:
			this.option = ((this.option - 1) % 5 + 5) % 5;
            AudioCache.playSfx("click");
			break;
		default:
			break;
	    
	    }
	}

	@Override
	public void keyreleased(GameKeys gk) {
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(SpriteBatch batch) {
		cam.setToOrtho(true, 528, 336);
		cam.zoom = 1f;
		cam.update(true);
		batch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    batch.draw(this.background, 
	      cam.viewportWidth / 2 - this.background.getRegionWidth() / 2,
	      cam.viewportHeight / 2 - this.background.getRegionHeight() / 2);

	    batch.setColor( 0, 0, 0, 1 );
	    font.draw(batch, "Controls", 198, 101);
	    font.draw(batch, "Options", 198, 131);
	    font.draw(batch, "Quit to Map", 198, 161);
	    font.draw(batch, "Quit to Menu", 198, 191);
	    font.draw(batch, "Quit to Desktop", 198, 221);
	    batch.setColor( 1, 1, 1, 1 );
	    batch.draw(this.arrow, 156, 96 + 30 * this.option);
	    String back = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.START)) + ": BACK TO GAME";
	    String howto = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.ATTACK)) 
	    		+  " OR " + Keys.toString(KeyMapping.gameKeyToInt(GameKeys.JUMP)) 
	    		+  ": SELECT ITEM";
	    font.draw(batch, back, 25, 25);
	    font.draw(batch, howto, 25, 55);
	}

}

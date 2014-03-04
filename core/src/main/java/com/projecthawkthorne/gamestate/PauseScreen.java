package com.projecthawkthorne.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;

public class PauseScreen extends GenericGamestate {

	private TextureRegion background = new TextureRegion(Assets.loadTexture("menu/pause.png"));
	private int option = 0;
	private TextureRegion arrow = new TextureRegion(Assets.loadTexture("menu/arrow.png"));
	private String musicFile = "daybreak";
	private OrthographicCamera cam = new OrthographicCamera(528, 336);
	private SpriteBatch batch = new SpriteBatch();

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		super.show();
		Assets.playMusic(musicFile);
	}

	@Override
	public void hide() {
		super.hide();
		Assets.stopMusic(musicFile);
	}

	@Override
	public void pause() {
		Assets.stopMusic(musicFile);
	}

	@Override
	public void resume() {
		Assets.playMusic(musicFile);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void keypressed(GameKeys gk) {
	    switch(gk){
		case DOWN:
			this.option = (this.option + 1) % 2;
            Assets.playSfx("click");
			break;
		case JUMP:
			Assets.playSfx("confirm");
			makeSelection(this.option);
			break;
		case SELECT:
			break;
		case UP:
			this.option = (this.option - 1 + 2) % 2;
            Assets.playSfx("click");
			break;
		case START:
			Gdx.app.getApplicationListener().dispose();
			Gdx.app.exit();
			break;
		default:
			break;
	    
	    }
	}

	private void makeSelection(int selection) {
		if(selection == 0){
			context.goBack();
		}else if(selection == 1){
			Gdx.app.exit();
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
	public void draw() {
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
	    BitmapFont font = Assets.getFont();
		font.setScale(0.8f, -0.8f);
		font.draw(batch, "Return to game", 198, 101);
	    font.draw(batch, "Quit", 198, 131);
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

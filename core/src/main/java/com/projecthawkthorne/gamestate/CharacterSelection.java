package com.projecthawkthorne.gamestate;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.projecthawkthorne.client.CharacterBundle;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Character;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;
import com.projecthawkthorne.content.Player;

public class CharacterSelection extends GenericGamestate {

	private TextureRegion background = new TextureRegion(Assets.loadTexture("menu/pause.png"));
	private int option = 0;
	private TextureRegion arrow = new TextureRegion(Assets.loadTexture("menu/arrow.png"));
	private String musicFile = "daybreak";
	private OrthographicCamera cam = new OrthographicCamera(HawkthorneGame.WIDTH, HawkthorneGame.HEIGHT);
	private SpriteBatch batch = context.getBatch();
	private String warning = "";
	private List<CharacterBundle> characterList = new ArrayList<CharacterBundle>();
	
	public CharacterSelection(){
		characterList.add(Assets.getCharacter("abed"));
		characterList.add(Assets.getCharacter("garrett"));
		characterList.add(Assets.getCharacter("annie"));
	}

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
	public void update(float dt){
		super.update(dt);
	}

	@Override
	public void keypressed(GameKeys gk) {
	    int cListSize = characterList.size();
		switch(gk){
		case DOWN:
			this.option = (this.option + 1) % cListSize;
            Assets.playSfx("click");
            warning = "";
			break;
		case JUMP:
			Assets.playSfx("confirm");
			makeSelection(this.option);
			break;
		case SELECT:
			break;
		case UP:
			this.option = (this.option - 1 + cListSize) % cListSize;
            Assets.playSfx("click");
            warning = "";
			break;
		case START:
			context.setScreen(context.lobby);
			break;
		default:
			break;
	    
	    }
	}

	private void makeSelection(int selection) {
		Gamestate level;
		warning = "";

		Player.getSingleton().setCharacter(new Character(characterList.get(selection).getName()));
		//note: mode should already be client anyways
		HawkthorneGame.MODE = Mode.CLIENT;
		context.setScreen(context.serverSelection);
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
	public void draw(Player p) {
		cam.setToOrtho(true, HawkthorneGame.WIDTH, HawkthorneGame.HEIGHT);
		cam.zoom = 1f;
		cam.update(true);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
	    batch.setColor( 1, 1, 1, 1 );
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    batch.draw(this.background, 
	      cam.viewportWidth / 2 - this.background.getRegionWidth() / 2,
	      cam.viewportHeight / 2 - this.background.getRegionHeight() / 2);

	    BitmapFont font = Assets.getFont();
		font.setScale(0.8f, -0.8f);
	    batch.setColor( 1, 1, 1, 1 );

	    for(int i = 0; i< characterList.size(); i++){
			batch.draw(characterList.get(i).getTexture(), 256, 106 + 48 * i, 48, 48);
		}
	    batch.draw(this.arrow, 230, 120 + 48 * this.option);
	    String back = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.START)) + ": GO BACK";
	    String howto = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.ATTACK)) 
	    		+  " OR " + Keys.toString(KeyMapping.gameKeyToInt(GameKeys.JUMP)) 
	    		+  ": SELECT ITEM";
	    font.draw(batch, back, 25, 25);
	    font.draw(batch, howto, 25, 55);
	    
	    
	    font.setColor(Color.RED);
	    font.draw(batch, warning, 60, 305);
	    
	    font.setColor(Color.WHITE);
	    batch.end();
	}

}

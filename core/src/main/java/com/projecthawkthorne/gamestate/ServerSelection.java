package com.projecthawkthorne.gamestate;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.socket.tcp.Results;
import com.projecthawkthorne.socket.tcp.Status;

public class ServerSelection extends GenericGamestate {

	private TextureRegion background = new TextureRegion(Assets.loadTexture("menu/pause.png"));
	private int option = 0;
	private TextureRegion arrow = new TextureRegion(Assets.loadTexture("menu/arrow.png"));
	private String musicFile = "daybreak";
	private OrthographicCamera cam = new OrthographicCamera(528, 336);
	private SpriteBatch batch = new SpriteBatch();
	private Results result = new Results();
	private List<List<String>> table = new ArrayList<List<String>>();
	private boolean initialized = false;
	
	public ServerSelection(){
		result.setStatus(Status.LOADING);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		super.show();
		initialized = false;
		result.setStatus(Status.LOADING);
		table.clear();
		
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
		if(!initialized){
			try {
				context.getQuery().getServerList(result, table);
			} catch (UnknownHostException e) {
				result.setStatus(Status.REQUEST_FAILED);
			}
			initialized = true;
		}
	}

	@Override
	public void keypressed(GameKeys gk) {
		int tableSize = table.size();
	    switch(gk){
		case DOWN:
			if(tableSize <= 0){
				Assets.playSfx("cancel");
			}else{
				this.option = (this.option + 1) %  tableSize;
				Assets.playSfx("click");
			}
			break;
		case JUMP:
			Assets.playSfx("confirm");
			if (tableSize > 0){
				makeSelection(this.option);
			}
			break;
		case SELECT:
			break;
		case UP:
			if(tableSize <= 0){
				Assets.playSfx("cancel");
			}else{
				this.option = (this.option - 1 + tableSize) % tableSize;
				Assets.playSfx("click");
			}
			break;
		case START:
			context.goBack();
			break;
		default:
			break;
	    
	    }
	}

	private void makeSelection(int selection) {
		try{
			String address = table.get(selection).get(0);
			int port = Integer.valueOf(table.get(selection).get(1));
			Player player = Player.getSingleton();
			player.registerPlayer(InetAddress.getByName(address), port);
//			Level level = Level.get(HawkthorneGame.START_LEVEL);
//			Level.switchState(level, level.getDoor("main"), player);
//			context.setScreen(level);
		}catch(UnknownHostException uhe){
			Gdx.app.log("Lobby", "unknown host");
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
	public void draw(Player p) {
		cam.setToOrtho(true, 528, 336);
		cam.zoom = 1f;
		cam.update(true);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

	    batch.setColor( 0, 0, 0, 1 );
	    BitmapFont font = Assets.getFont();
	    font.setColor(Color.GREEN);
		font.setScale(0.8f, -0.8f);

		int offset = 30;
		if(result.getStatus()!=Status.SUCCESS){
			font.draw(batch, result.getStatus().toString(), 128, 77);
		}else if(table.size() <= 0){
			font.draw(batch, "No servers found", 128, 77);
		}else{
			font.draw(batch, "IP Address", 98, 101 - offset);
			font.draw(batch, "Port", 310, 101 - offset);
			font.draw(batch, "Players", 410, 101 - offset);
			for (int i = 0; i < table.size(); i++) {
				List<String> row = table.get(i);
				font.draw(batch, row.get(0), 98, 101 + offset * i);
				font.draw(batch, row.get(1), 310, 101 + offset * i);
				font.draw(batch, row.get(2), 410, 101 + offset * i);
			}
		}
		
	    batch.setColor( 1, 1, 1, 1 );
	    batch.draw(this.arrow, 70, 96 + 30 * this.option);
	    String back = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.START)) + ": GO BACK";
	    String howto = Keys.toString(KeyMapping.gameKeyToInt(GameKeys.ATTACK)) 
	    		+  " OR " + Keys.toString(KeyMapping.gameKeyToInt(GameKeys.JUMP)) 
	    		+  ": SELECT ITEM";
	    font.draw(batch, back, 25, 25);
	    font.draw(batch, howto, 25, 55);
	    font.setColor(Color.WHITE);
	    batch.end();
	}

}

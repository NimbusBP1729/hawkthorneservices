package com.projecthawkthorne.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.GenericGamestate;
import com.projecthawkthorne.socket.tcp.QueryInterface;
import com.projecthawkthorne.socket.udp.Client;
import com.projecthawkthorne.socket.udp.Command;
import com.projecthawkthorne.socket.udp.MessageBundle;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneGame extends Game {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	
	public static final String START_LEVEL = "town";
	public static Mode MODE = Mode.SERVER;
	public String trackedLevel = START_LEVEL;
	protected Player trackedPlayer;
	protected long lastTime = 0;
	private Screen lastScreen;
	private HawkthorneUserInterface userInterface;
	private long lastPositionBroadcast = System.currentTimeMillis();
	private QueryInterface query;
	private Skin skin;
	
	@Override
	public void create() {
		Assets.load();
		skin = new Skin();
		LabelStyle ls = new LabelStyle();
		ls.font = Assets.getFont();
		ls.fontColor = Color.WHITE;
		skin.add("default", ls);

		skin.add("header", ls);
		
		ScrollPaneStyle sps = new ScrollPaneStyle();
		skin.add("default", sps);
		
		TextButtonStyle tbs = new TextButtonStyle();
		tbs.down = new NinePatchDrawable(Assets.loadNinePatch("button.png"));
		tbs.up = new NinePatchDrawable(Assets.loadNinePatch("button.png"));
		tbs.font = Assets.getFont();
		tbs.fontColor = Color.WHITE;
		skin.add("default", tbs);
		
		TextFieldStyle tfs = new TextFieldStyle();
		tfs.font = Assets.getFont();
		tfs.fontColor = Color.WHITE;
		skin.add("default", tfs);
		
		
		
		query = new QueryInterface(this);
		
		Gdx.input.setCatchBackKey(true);
		userInterface = new HawkthorneUserInterface();
		Gdx.input.setInputProcessor(userInterface);
		Gamestate.setContext(this);
		
		Screen lobby = GenericGamestate.get("lobby");
		this.setScreen(lobby);
	}

	@Override
	public final void render() {
		Timer.updateTimers();
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;

		//handles all time updates
		this.getScreen().render(dt/1000f);
		this.userInterface.update(dt);
		
		//handles all drawing to the screen
		((Gamestate) this.getScreen()).draw();
		this.userInterface.draw();
		if (HawkthorneGame.MODE == Mode.CLIENT) {
			Player player = Player.getSingleton();

			Client client = player.getClient();
			if(client == null){
				return;
			}
			MessageBundle msg;
			int msgCount = 0;
			long processingDuration = System.currentTimeMillis();
			while ((msg = client.receive()) != null) {
				client.handleMessage(msg);
				msgCount++;
			}
			processingDuration = System.currentTimeMillis()
					- processingDuration;

			// must be called together
			// updateStatus(msgCount,processingDuration);
			// printStatusPeriodically();

			
			if (currentTime - this.lastPositionBroadcast > 50) {
				MessageBundle mb = new MessageBundle();
				mb.setEntityId(player.getId());
				mb.setCommand(Command.POSITIONVELOCITYUPDATE);
				String x = Float.toString(MathUtils.round(player.x));
				String y = Float.toString(MathUtils.round(player.y));
				String vX = Float.toString(MathUtils.round(player.velocityX));
				String vY = Float.toString(MathUtils.round(player.velocityY));
				mb.setParams(x, y, vX, vY, player.getState().toString(),
						player.getDirectionsAsString());
				this.lastPositionBroadcast = currentTime;
				client.send(mb);
			}
		}
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		userInterface.resize(width, height);
	}
	
	@Override
	public void setScreen(Screen screen){
		this.lastScreen = this.getScreen();
		super.setScreen(screen);
	}
	public void setScreen(String screenName){
		setScreen(GenericGamestate.get(screenName));
	}

	public void goBack() {
		this.setScreen(this.lastScreen);
	}

	public HawkthorneUserInterface getControlsOverlay() {
		return userInterface;
	}

	public QueryInterface getQuery() {
		return query;
	}

	public Skin getSkin() {
		return skin;
	}

}

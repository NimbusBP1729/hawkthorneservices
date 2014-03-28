package com.projecthawkthorne.client;

import java.net.SocketException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.gamestate.CharacterSelection;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.GenericGamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.Lobby;
import com.projecthawkthorne.gamestate.PauseScreen;
import com.projecthawkthorne.gamestate.ServerSelection;
import com.projecthawkthorne.socket.tcp.QueryInterface;
import com.projecthawkthorne.socket.udp.Client;
import com.projecthawkthorne.socket.udp.Command;
import com.projecthawkthorne.socket.udp.MessageBundle;
import com.projecthawkthorne.socket.udp.Server;
import com.projecthawkthorne.timer.Timer;

public class HawkthorneGame extends MyGame {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	
	//private OrthographicCamera cam;
	public static Mode MODE;
	public static final String START_LEVEL = "town";
	public String trackedLevel = START_LEVEL;
	public Player trackedPlayer;
	protected float trackingX = 0;
	protected float trackingY = 0;
	protected long lastTime = 0;
	protected long lastPositionBroadcast = System.currentTimeMillis();
	protected static final boolean IS_Y_DOWN = false;
	private static final Level[] EMPTY_LEVEL_ARRAY = new Level [0];
	

	private long lastIterationInfo = 0;
	private long processingDurationSum = 0;
	private int processingCountSum = 0;
	private int processingIterations = 0;
	
	//SERVER VARIABLES
	private MessageBundle mb = new MessageBundle();
	private HawkthorneUserInterface userInterface;
	private Gamestate lastScreen;
	private QueryInterface query;
	private SpriteBatch batch;
	
	public GenericGamestate serverSelection;
	public GenericGamestate characterSelection;
	public GenericGamestate lobby;
	public GenericGamestate pause;
	
	@Override
	public void create() {
		Assets.load();
		userInterface = new HawkthorneUserInterface();
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(userInterface);

		batch = new SpriteBatch();
		Gamestate.setContext(this);
		query = new QueryInterface(this);
		
		
		//define screens
		serverSelection = new ServerSelection();
		characterSelection = new CharacterSelection();
		lobby = new Lobby();
		pause = new PauseScreen();

		//set down some screens, yup
		switch(Gdx.app.getType()){
		case Desktop:
			this.setScreen(lobby);
			break;
		default:
			HawkthorneGame.MODE = Mode.CLIENT;
			this.setScreen(characterSelection);
			break;
		}
	}
	
	protected void updateStatus(int msgCount, long processingDuration) {
		processingDurationSum += processingDuration;
		processingCountSum += msgCount;
		processingIterations++;
	}

	protected final void printStatusPeriodically() {
		long now = System.currentTimeMillis();
		if(now-lastIterationInfo > 30000){
			System.out.println("avg. processing duration=="+1.0f*processingDurationSum/processingIterations);
			System.out.println("avg. msg processed      =="+1.0f*processingCountSum/processingIterations);
			System.out.println("iterations              =="+processingIterations);
			System.out.println("================================================");

			lastIterationInfo = now;
		}
	}

	@Override
	public void render() {
		Timer.updateTimers();
		
		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		
		if(HawkthorneGame.MODE == Mode.CLIENT){

			//receive info
			Client client = Player.getSingleton().getClient();
			if (client != null) {
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
				updateStatus(msgCount, processingDuration);
				printStatusPeriodically();
			}
			Player player = Player.getSingleton();
			Gamestate gs = (Gamestate) this.getScreen();

			//updates
			Set<Player> players = gs.getPlayers();
			for (Player p : players) {
				p.update(dt);
			}
			gs.update(dt/1000f);

			//drawing
			gs.draw(player);

			//send info
			if (currentTime - this.lastPositionBroadcast > 50 && client != null) {
				MessageBundle mb = new MessageBundle();
				mb.setEntityId(player.getId());
				mb.setCommand(Command.POSITIONVELOCITYUPDATE);
				String x = Float.toString(roundTwoDecimals(player.x));
				String y = Float.toString(roundTwoDecimals(player.y));
				String vX = Float.toString(roundTwoDecimals(player.velocityX));
				String vY = Float.toString(roundTwoDecimals(player.velocityY));
				mb.setParams(x, y, vX, vY, player.getState().toString(),player.getDirectionsAsString());
				this.lastPositionBroadcast = currentTime;
				client.send(mb);
			}

		}else if(HawkthorneGame.MODE == Mode.SERVER){
			Server server = null;
			try {
				server = Server.getSingleton();
			} catch (SocketException e) {
				Gdx.app.error("game", "server may already be created");
			}

			//receive info
			int msgCount = 0;
			long processingDuration = System.currentTimeMillis();
			for (MessageBundle msg = server.receive();
					msg != null;
					msg = server.receive()) {
				server.handleMessage(msg);
				msgCount++;
			}
			processingDuration = System.currentTimeMillis() - processingDuration;
			
			updateStatus(msgCount,processingDuration);		
			printStatusPeriodically();
			
			//update
			Map<String, Level> levels = Level.getLevelMap();
			Level [] lvls = levels.values().toArray(EMPTY_LEVEL_ARRAY);
			for (Level level : lvls) {
				Set<Player> players = level.getPlayers();
				for (Player player : players) {
					player.update(dt);
				}
				level.update(dt/1000f);
			}

			//drawing
			if (trackedPlayer == null) {
				Level.get(trackedLevel).draw(trackedPlayer);
			} else {
				trackedPlayer.getLevel().draw(trackedPlayer);
			}
			
			//send info
			if (currentTime - this.lastPositionBroadcast > 50) {
				for (Entry<UUID, Player> entry : Player.getPlayerMap()
						.entrySet()) {
					mb.setEntityId(entry.getKey());
					mb.setCommand(Command.POSITIONVELOCITYUPDATE);
					Player player = entry.getValue();
					String x = Float.toString(roundTwoDecimals(player.x));
					String y = Float.toString(roundTwoDecimals(player.y));
					String vX = Float.toString(roundTwoDecimals(player.velocityX));
					String vY = Float.toString(roundTwoDecimals(player.velocityY));
					mb.setParams(x, y, vX, vY, player.getState().toString(),player.getDirectionsAsString());
					this.lastPositionBroadcast = currentTime;
					server.sendToAllExcept(mb, entry.getKey());
				}
			}		

		} else{
			Gamestate gs = (Gamestate) this.getScreen();
			gs.update(dt/1000f);
			gs.draw(null);
		}
		
		userInterface.draw();

	}

	private float roundTwoDecimals(float val) {
		return MathUtils.round(val*100)/100.0f;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

	public HawkthorneUserInterface getUserInterface() {
		return userInterface;
	}

	public void goBack() {
		if(lastScreen==null){
			Gdx.app.exit();
		}else{
			this.setScreen(lastScreen);
		}
	}
	
	@Override
	public void setScreen(Gamestate newLevel){
		this.lastScreen = this.getScreen();
		super.setScreen(newLevel);
	}

	public QueryInterface getQuery() {
		return query;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

}

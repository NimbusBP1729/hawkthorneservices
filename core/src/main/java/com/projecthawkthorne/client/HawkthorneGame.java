package com.projecthawkthorne.client;

import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.projecthawkthorne.content.MathUtils;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.socket.Client;
import com.projecthawkthorne.socket.Command;
import com.projecthawkthorne.socket.MessageBundle;

/**
 * Note: should really be named HawkthorneClientGame, but I didn't want to cause
 * additional breakage to other modules
 * @author Pat
 *
 */
public class HawkthorneGame extends HawkthorneParentGame {
	protected final Player trackedPlayer;
	
	public HawkthorneGame(){
		HawkthorneGame.MODE = Mode.CLIENT;
		trackedPlayer = Player.getSingleton();
	}

	public HawkthorneGame(String[] args) {
		this();
		Player.getSingleton().setUsername(args[0]);
	}

	@Override
	public void create() {
		super.create();
		Level level = Level.get(START_LEVEL);
		Level.switchState(level, level.getDoor("main"), Player.getSingleton());
	}

	@Override
	public void render() {
		super.render();

		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		
		Client client = Client.getSingleton();
		MessageBundle msg;
		int msgCount = 0;
		long processingDuration = System.currentTimeMillis();
		while ((msg = client.receive()) != null) {
			client.handleMessage(msg);
			msgCount++;
		}
		processingDuration = System.currentTimeMillis()-processingDuration;
		
		//must be called together
		updateStatus(msgCount,processingDuration);		
		printStatusPeriodically();
			
		Player player = Player.getSingleton();
		Gamestate level = player.getLevel();
		if (level instanceof Level) {
			levelRender((Level) level, player);
		} else {
			throw new UnsupportedOperationException("non-level gamestates aren't supported yet");
		}
		
		player.processKeyActions();
		Set<Player> players = level.getPlayers();
		for (Player p : players) {
			p.update(dt);
		}
		level.update(dt);

		if (currentTime - this.lastPositionBroadcast > 50) {
			MessageBundle mb = new MessageBundle();
			mb.setEntityId(player.getId());
			mb.setCommand(Command.POSITIONVELOCITYUPDATE);
			String x = Float.toString(MathUtils.roundTwoDecimals(player.x));
			String y = Float.toString(MathUtils.roundTwoDecimals(player.y));
			String vX = Float.toString(MathUtils
					.roundTwoDecimals(player.velocityX));
			String vY = Float.toString(MathUtils
					.roundTwoDecimals(player.velocityY));
			mb.setParams(x, y, vX, vY, player.getState().toString(),player.getDirectionsAsString());
			this.lastPositionBroadcast = currentTime;
			client.send(mb);
		}
	}

	@Override
	protected void levelRender(Level level, Player player) {
		super.levelRender(level, player);
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			player.die();
		}
	}

}

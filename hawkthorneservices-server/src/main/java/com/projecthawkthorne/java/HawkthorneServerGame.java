package com.projecthawkthorne.java;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.HawkthorneParentGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.MathUtils;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.socket.Command;
import com.projecthawkthorne.socket.MessageBundle;
import com.projecthawkthorne.socket.Server;

public class HawkthorneServerGame extends HawkthorneParentGame {
	
	boolean switchCharacterKeyDown = false;
	private int trackedPlayerIndex = 0;
	
	public HawkthorneServerGame(){
		HawkthorneGame.MODE = Mode.SERVER;
	}

	@Override
	public void render() {
		super.render();

		long currentTime = System.currentTimeMillis();
		long dt = (currentTime - this.lastTime);
		this.lastTime = currentTime;
		long maxDt = 100;
		dt = maxDt < dt ? maxDt : dt;
		
		Server server = Server.getSingleton();

		for (MessageBundle msg = server.receive();
				msg != null;
				msg = server.receive()) {
			server.handleMessage(msg);
		}
		Map<String, Level> levels = Level.getLevelMap();
		if (trackedPlayer == null) {
			levelRender(Level.get(trackedLevel), null);
		} else {
			levelRender((Level) trackedPlayer.getLevel(), trackedPlayer);
		}

		for (Level level : levels.values()) {
			Set<Player> players = level.getPlayers();
			for (Player player : players) {
				player.update(dt);
			}
			level.update(dt);
		}
		if (currentTime - this.lastPositionBroadcast > 50) {
			for (Entry<UUID, Player> entry : Player.getPlayerMap()
					.entrySet()) {
				MessageBundle mb = new MessageBundle();
				mb.setEntityId(entry.getKey());
				mb.setCommand(Command.POSITIONVELOCITYUPDATE);
				Player player = entry.getValue();
				String x = Float.toString(MathUtils.roundTwoDecimals(player.x));
				String y = Float.toString(MathUtils.roundTwoDecimals(player.y));
				String vX = Float.toString(MathUtils.roundTwoDecimals(player.velocityX));
				String vY = Float.toString(MathUtils.roundTwoDecimals(player.velocityY));
				mb.setParams(x, y, vX, vY, player.getState().toString(),player.getDirectionsAsString());
				this.lastPositionBroadcast = currentTime;
				Server.getSingleton().sendToAllExcept(mb, entry.getKey());
			}
		}		
	}
	
	@Override
	protected void levelRender(Level level, Player player) {
		super.levelRender(level, player);
		
		if (!switchCharacterKeyDown 
				&& Gdx.input.isKeyPressed(Keys.S)
				&& level.getPlayers().size()>0) {
			Player[] players = level.getPlayers().toArray(new Player[0]);
			trackedPlayerIndex = (trackedPlayerIndex+1)%players.length;
			trackedPlayer = players[trackedPlayerIndex];
		}
		switchCharacterKeyDown = Gdx.input.isKeyPressed(Keys.S);
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			if(trackedPlayer!=null){
				trackedLevel = trackedPlayer.getLevel().getName();
			}
			trackedPlayer = null;
		}
		int boost = 0;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
				|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
			boost = 6;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			trackingX -= (5 + boost);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			trackingX += (5 + boost);
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			trackingY += (5 + boost);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			trackingY -= (5 + boost);
		}
	}
}

package com.projecthawkthorne.client;

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
		while ((msg = client.receive()) != null) {
			client.handleMessage(msg);
		}
		Player player = Player.getSingleton();
		Gamestate gs = player.getLevel();
		player.processKeyActions();
		player.update(dt);
		gs.update(dt);
		if (gs instanceof Level) {
			levelRender((Level) gs, player);
		} else {
			throw new UnsupportedOperationException("non-level gamestates aren't supported yet");
		}
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
			mb.setParams(x, y, vX, vY);
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

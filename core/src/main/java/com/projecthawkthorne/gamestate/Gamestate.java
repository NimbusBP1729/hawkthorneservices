package com.projecthawkthorne.gamestate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Node;

public abstract class Gamestate{
	protected static HawkthorneGame context;
	private Set<Player> players = new HashSet<Player>();
	
	public abstract String getName();

	final void addPlayer(Player player) {
		if (players.contains(player)) {
			Gdx.app.error(
					"player adding error",
					"level '" + this.getName() + "' already contains "
							+ player.getId() + ":" + player.getUsername());
		} else {
			players.add(player);
		}
	}

	final boolean removePlayer(Player p) {
		return players.remove(p);
	}

	public final Set<Player> getPlayers() {
		return players;
	}

	public Door getDoor(String doorName) {
		throw new UnsupportedOperationException("this class:("
				+ this.getClass().getName() + ") has no doors");
	}

	public Map<UUID, Node> getNodeMap() {
		throw new UnsupportedOperationException("this class:("
				+ this.getClass().getName() + ") has no nodes");
	}

	public abstract void draw(Player player);

	public static final void setContext(HawkthorneGame game) {
		context = game;
	}

	public static HawkthorneGame getContext() {
		return context;
	}

	public abstract void hide();

	public abstract void pause();

	public abstract void resume();

	public abstract void show();

	public abstract void resize(int width, int height);


	public abstract void dispose();
	
	public abstract void update(float dt);
//	
//	public final void render(){throw new UnsupportedOperationException();}
//	public final void render(long dt){throw new UnsupportedOperationException();}
//	public final void render(float dt){throw new UnsupportedOperationException();}
//

}

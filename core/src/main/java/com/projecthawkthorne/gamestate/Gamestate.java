package com.projecthawkthorne.gamestate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projecthawkthorne.client.HawkthorneParentGame;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Node;

public abstract class Gamestate implements Screen{
	protected static HawkthorneParentGame context;
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

	public abstract void draw(SpriteBatch batch);

	public static final void setContext(HawkthorneParentGame game) {
		context = game;
	}

	public static HawkthorneParentGame getContext() {
		return context;
	}


}

package com.projecthawkthorne.gamestate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Node;

public abstract class Gamestate {

	private Set<Player> players = new HashSet<Player>();

	public abstract String getName();

	final void addPlayer(Player player) {
		if (players.contains(player)) {
			System.err.println("player has already been added to "
					+ this.getName());
		} else {
			players.add(player);
		}
	}

	final boolean removePlayer(Player p) {
		return players.remove(p);
	}

	public abstract void update(long dt);

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

}

package com.projecthawkthorne.gamestate;

import java.util.HashSet;
import java.util.Set;

import com.projecthawkthorne.server.Player;
import com.projecthawkthorne.server.nodes.Door;

public class Overworld implements Gamestate {
	private static final String name = "overworld";
	private Set<Player> players = new HashSet<Player>(3);

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addPlayer(Player player) {
		if (players.contains(player)) {
			System.err.println("player has already been added to overworld");
		} else {
			players.add(player);
		}
	}

	@Override
	public boolean removePlayer(Player p) {
		return players.remove(p);
	}

	@Override
	public LevelMap getNodes() {
		return null;
	}

	@Override
	public Door getDoor(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}
}

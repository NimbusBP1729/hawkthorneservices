package com.projecthawkthorne.gamestate;

import java.util.Set;

import com.projecthawkthorne.server.Player;
import com.projecthawkthorne.server.nodes.Door;

public interface Gamestate {

	String getName();

	void addPlayer(Player player);

	boolean removePlayer(Player p);

	LevelMap getNodes();

	Door getDoor(String string);

	void update();

	Set<Player> getPlayers();
}

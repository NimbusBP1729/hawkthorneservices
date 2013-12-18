package com.projecthawkthorne.gamestate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.projecthawkthorne.server.nodes.Node;

public class LevelMap {
	Map<Integer, Node> map = new HashMap<Integer, Node>();

	public void put(int id, Node node) {
		map.put(id, node);
	}

	public Collection<Node> values() {
		return map.values();
	}

	public Node remove(int id) {
		return map.remove(id);
	}

}

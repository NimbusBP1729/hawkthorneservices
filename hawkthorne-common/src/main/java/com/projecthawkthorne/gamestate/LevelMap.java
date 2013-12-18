package com.projecthawkthorne.gamestate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.projecthawkthorne.server.nodes.Node;

public class LevelMap {
	Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();

	public void put(int id, Node node) {
		nodeMap.put(id, node);
	}

	public Collection<Node> values() {
		return nodeMap.values();
	}

	public Node remove(int id) {
		return nodeMap.remove(id);
	}

}

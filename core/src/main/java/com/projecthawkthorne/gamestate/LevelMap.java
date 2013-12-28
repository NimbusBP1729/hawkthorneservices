package com.projecthawkthorne.gamestate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.projecthawkthorne.content.nodes.Node;

public class LevelMap {
	Map<String, Node> nodeMap = new HashMap<String, Node>();

	public void put(String id, Node node) {
		nodeMap.put(id, node);
	}

	public Collection<Node> values() {
		return nodeMap.values();
	}

	public Node remove(String id) {
		return nodeMap.remove(id);
	}

}

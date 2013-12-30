package com.projecthawkthorne.timer;

import java.util.HashSet;
import java.util.Set;

/**
 * sample class used to play a timer
 * 
 * @author Patrick
 * 
 */
class TestTimeable implements Timeable {
	Set<String> map = new HashSet<String>();

	public Set<String> getMap() {
		return map;
	}

	@Override
	public void handleTimer(String name) {
		// simply adds all strings to map when the time is up
		map.add(name);
	}

}

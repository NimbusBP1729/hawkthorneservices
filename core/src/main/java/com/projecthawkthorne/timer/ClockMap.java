package com.projecthawkthorne.timer;

import java.util.HashMap;
import java.util.Iterator;

class ClockMap {
	private HashMap<NameAndCaller, Long> clocks = new HashMap<NameAndCaller, Long>();

	boolean containsKey(NameAndCaller nac) {
		Iterator<NameAndCaller> it = clocks.keySet().iterator();
		NameAndCaller nac2;
		while (it.hasNext()) {
			nac2 = it.next();
			if (nac2.equals(nac)) {
				return true;
			}
		}
		return false;
	}

	void put(NameAndCaller nac, long l) {
		clocks.put(nac, l);
	}

	Long remove(NameAndCaller nac) {
		return clocks.remove(nac);
	}

	long get(NameAndCaller nac) {
		return clocks.get(nac);
	}

	NameAndCaller[] keySet() {
		return clocks.keySet().toArray(new NameAndCaller[0]);
	}

	boolean isEmpty() {
		return clocks.isEmpty();
	}

}

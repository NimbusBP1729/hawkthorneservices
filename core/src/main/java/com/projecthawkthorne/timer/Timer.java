/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.timer;

import java.util.HashMap;


/**
 * 
 * @author Patrick
 */
public class Timer {
	private static final NameAndCaller[] EMPTY_NAMEANDCALLER_ARRAY = new NameAndCaller[0];
	private static HashMap<NameAndCaller, Long> clocks = new HashMap<NameAndCaller, Long>();
	private static NameAndCaller nac = new NameAndCaller(null, null);
	
	public static boolean add(long delay, String name, Timeable caller) {
		NameAndCaller nac = new NameAndCaller(name, caller);
		if (clocks.containsKey(nac)) {
			return false;
		}
		long cur = System.currentTimeMillis();
		clocks.put(nac, cur + delay);
		return true;
	}



	/**
	 * cancels a timer
	 * 
	 * @param name
	 *            the name of the timer
	 * @param caller
	 *            the object using the timer
	 */
	public static void cancel(String name, Timeable caller) {
		nac.setName(name);
		nac.setCaller(caller);
		Long success = clocks.remove(nac);
		if (success == null) {
			System.err.println("A timer with the name: '" + name
					+ "' does not exist");
		}
	}

	/**
	 * returns true if the class contains a timer for this object
	 * 
	 * @param name
	 *            the name of the timer
	 * @param caller
	 *            the object using the timer
	 * @return true if the class contains a timer for this object
	 */
	public static boolean contains(String name, Timeable caller) {
		NameAndCaller nac = new NameAndCaller(name, caller);
		return clocks.containsKey(nac);
	}

	/**
	 * returns true if the timer has expired
	 * 
	 * @param name
	 *            the name of the timer
	 * @param caller
	 *            the object using the timer
	 * @return true if the timer has expired
	 */
	public static boolean hasExpired(String name, Timeable caller) {
		nac.setName(name);
		nac.setCaller(caller);
		if (!clocks.containsKey(nac)) {
			System.err.println("No timer with the name: '" + name + "' exists");
			return false;
		}
		long curTime = System.currentTimeMillis();
		boolean result = clocks.get(nac) < curTime;
		return result;
	}

	/**
	 * checks all timers to see if the have expired and <br>
	 * handles them accordingly
	 */
	public static void updateTimers() {
		for (NameAndCaller nac : clocks.keySet().toArray(EMPTY_NAMEANDCALLER_ARRAY)) {
			if (hasExpired(nac.getName(), nac.getCaller())) {
				Timeable t = nac.getCaller();
				t.handleTimer(nac.getName());
				cancel(nac.getName(), nac.getCaller());
			}
		}
	}

}

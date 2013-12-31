/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.timer;

/**
 * 
 * @author Patrick
 */
public class Timer {

	private static ClockMap clocks = new ClockMap();

	public static boolean add(long delay, String name, Timeable caller) {
		NameAndCaller nac = new NameAndCaller(name, caller);
		if (clocks.containsKey(nac)) {
			System.err.println("clock '" + name + "' was already inserted");
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
		NameAndCaller nac = new NameAndCaller(name, caller);
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
		NameAndCaller nac = new NameAndCaller(name, caller);
		if (!clocks.containsKey(nac)) {
			System.err.println("No timer with the name: '" + name + "' exists");
			return false;
		}
		long curTime = System.currentTimeMillis();
		boolean result = clocks.get(nac) < curTime;
		// System.err.println(result);
		return result;
	}

	/**
	 * checks all timers to see if the have expired and <br>
	 * handles them accordingly
	 */
	public static void updateTimers() {
		NameAndCaller[] clockKeys = clocks.keySet();
		NameAndCaller nac;
		for (int i = 0; i < clockKeys.length; i++) {
			nac = clockKeys[i];
			if (hasExpired(nac.name, nac.caller)) {
				Timeable t = nac.caller;
				t.handleTimer(nac.name);
				cancel(nac.name, nac.caller);
			}
		}
	}

	public static boolean isEmpty() {
		return clocks.isEmpty();
	}
}

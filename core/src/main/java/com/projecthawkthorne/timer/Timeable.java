package com.projecthawkthorne.timer;

public interface Timeable {

	/**
	 * the implementing class calls this function when name has expired
	 * 
	 * @param name
	 */
	void handleTimer(String name);

}

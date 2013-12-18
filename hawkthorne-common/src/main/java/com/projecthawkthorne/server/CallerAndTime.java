package com.projecthawkthorne.server;

import com.projecthawkthorne.timer.Timeable;


public class CallerAndTime {
	private Timeable caller;
	private long expiration;

	public CallerAndTime(Timeable callerIn, long d) {
		this.caller = callerIn;
		this.expiration = d;
	}

	public long getExpiration() {
		return this.expiration;
	}

	/**
	 * @return the caller
	 */
	public Timeable getCaller() {
		return caller;
	}

}
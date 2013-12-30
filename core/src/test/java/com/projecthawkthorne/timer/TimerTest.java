package com.projecthawkthorne.timer;

import junit.framework.TestCase;

public class TimerTest extends TestCase {
	/**
	 * a timeable class that just adds values to a map
	 */
	TestTimeable timeable;

	public TimerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		timeable = new TestTimeable();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		timeable = null;

	}

	public void testTimerDoesntExpireEarly() throws InterruptedException {
		Timer.add(2000, "early", timeable);
		Thread.sleep(1000);
		Timer.updateTimers();
		assert (!timeable.getMap().contains("early"));
	}

	public void testTimerExpires() throws InterruptedException {
		Timer.add(2000, "on time", timeable);
		Thread.sleep(2500);
		Timer.updateTimers();
		assert (timeable.getMap().contains("on time"));
	}

	/**
	 * disallow double insertion of a timer
	 * 
	 * @throws InterruptedException
	 */
	public void testLastInsertion() throws InterruptedException {
		Timer.add(2000, "double", timeable);
		try {
			Timer.add(1000, "double", timeable);
			assert (false);
		} catch (AssertionError e) {
		}
	}

}

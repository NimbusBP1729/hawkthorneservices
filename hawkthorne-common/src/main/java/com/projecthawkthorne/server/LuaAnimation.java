/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server;

import java.util.List;

/**
 * this class is a stub. it always returns a position of 1
 * @author Patrick
 */
public class LuaAnimation {

  protected int currentPosition;
  protected List<Integer> frameTimes;
  protected String status = "playing";
  private int position = 1;
  private long timeRemainder = 0;
  private long timeMax = 2;

//  public LuaAnimation(Mode mode, String frames, String times) {
//  }
  public LuaAnimation(){
  }

  public void update(long dt) {
	  timeRemainder = (timeRemainder+dt)%timeMax+1;
//	  position = safeLongToInt(timeRemainder);
//    currentPosition += dt;
//    for (int i = 0; i < frameTimes.get(i); i++) {
//    }
  }
  
  public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}

  /**
   * the position in the animation
   * @return frame position in sequence
   */
  public int getPosition() {
    return position;
  }
}

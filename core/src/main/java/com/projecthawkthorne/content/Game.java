/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.content;

/**
 * 
 * @author Patrick
 */
public final class Game {

	public static final float xFactor = 0.001f;
	public static final float step = 0.009f;
	public static final float friction = (float) (0.146875 * Game.step);
	public static final float accel = (float) (0.046875 * Game.step);
	public static final float deccel = (float) (0.5 * Game.step);
	public static final float gravity = -(float) (0.21875 * Game.step);
	public static final long fall_grace = 75;
	public static final float fall_dps = 0; // 2;
	public static final float airaccel = (float) (0.09375 * Game.step);
	public static final float airdrag = (float) (0.096875 * Game.step);
	public static final float max_x = .3f;
	public static final float maxVelocityY = .6f;

	public static boolean DEBUG = true;

}

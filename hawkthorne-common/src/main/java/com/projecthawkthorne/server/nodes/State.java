package com.projecthawkthorne.server.nodes;

public enum State {
	DEFAULT,
	// enemies
	DYING,
	// players
	IDLE, JUMP, GAZEWALK, WALK, CROUCH, WIELDJUMP, HOLDWALK, HOLDJUMP, CROUCHWALK, WIELDWALK, GAZE, HOLD, ATTACKJUMP, ATTACKWALK, GAZEIDLE, DEAD, WIELDIDLE, HURT, DYINGATTACK,
	// players and enemies
	ATTACK

}

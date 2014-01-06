package com.projecthawkthorne.socket;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.Player;

public class ClientMessageHandlingTest {

	private Client client;
	private float delta = 0.01f;

	@Before
	public void setUp() throws Exception {
		HawkthorneGame.MODE = Mode.CLIENT;
		client = Client.getSingleton();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClientPositionUpdate() {
		//create client side player
		Player p = Player.getSingleton();
		p.x = 100;
		p.y = 200;
		p.velocityX = 300;
		p.velocityY = 400;

		//create a received message
		MessageBundle msg =new MessageBundle();
		msg.setCommand(Command.POSITIONVELOCITYUPDATE);
		msg.setEntityId(p.getId());
		msg.setParams("60000","4.7","-6.9","-8");

		//process message
		client.handleMessage(msg);
		
		//verify correctness of message handling
		assertEquals(Float.parseFloat("60000"), p.x,delta);
		assertEquals(Float.parseFloat("4.7"), p.y,delta);
		assertEquals(Float.parseFloat("-6.9"), p.velocityX,delta);
		assertEquals(Float.parseFloat("-8"), p.velocityY,delta);		
	}

}

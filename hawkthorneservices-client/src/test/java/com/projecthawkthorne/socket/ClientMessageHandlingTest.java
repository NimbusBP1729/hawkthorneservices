package com.projecthawkthorne.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.State;

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
		p.setState(State.CROUCH);

		//create a received message
		MessageBundle msg =new MessageBundle();
		msg.setCommand(Command.POSITIONVELOCITYUPDATE);
		msg.setEntityId(p.getId());
		msg.setParams("60000","4.7","-6.9","-8", "WALK","0011");

		//process message
		client.handleMessage(msg);
		
		//TODO: determine the weighting
		// currently it is 0.5 so the numbers are averages
		
		//verify correctness of message handling
		assertEquals(Float.parseFloat("30050"), p.x,delta);
		assertEquals(Float.parseFloat("102.35"), p.y,delta);
		assertEquals(Float.parseFloat("146.55"), p.velocityX,delta);
		assertEquals(Float.parseFloat("196"), p.velocityY,delta);
		assertEquals("WALK", p.getState().toString());
	}


	@Test
	public void testKeyPressedWhenUnpressed() {
		// create known connected player
		UUID id = UUID.randomUUID();
		Player player = Player.getConnectedPlayer(id);
		player.setIsKeyDown(GameKeys.ATTACK, false);

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.KEYPRESSED);
		msg.setEntityId(id);
		msg.setParams("ATTACK");

		// process message
		client.handleMessage(msg);

		// confirm validity of message processing
		assertTrue(player.getIsKeyDown(GameKeys.ATTACK));

	}

	@Test
	public void testKeyPressedWhenPressed() {
		// create known connected player
		UUID id = UUID.randomUUID();
		Player player = Player.getConnectedPlayer(id);
		player.setIsKeyDown(GameKeys.ATTACK, true);

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.KEYPRESSED);
		msg.setEntityId(id);
		msg.setParams("ATTACK");

		// process message
		client.handleMessage(msg);

		// confirm validity of message processing
		assertTrue(player.getIsKeyDown(GameKeys.ATTACK));

	}

	@Test
	public void testKeyReleasedWhenPressed() {
		// create known connected player
		UUID id = UUID.randomUUID();
		Player player = Player.getConnectedPlayer(id);
		player.setIsKeyDown(GameKeys.JUMP, true);

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.KEYRELEASED);
		msg.setEntityId(id);
		msg.setParams("JUMP");

		// process message
		client.handleMessage(msg);

		// confirm validity of message processing
		assertFalse(player.getIsKeyDown(GameKeys.JUMP));

	}

	@Test
	public void testKeyReleasedWhenUnpressed() {
		// create known connected player
		UUID id = UUID.randomUUID();
		Player player = Player.getConnectedPlayer(id);
		player.setIsKeyDown(GameKeys.JUMP, false);

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.KEYRELEASED);
		msg.setEntityId(id);
		msg.setParams("JUMP");

		// process message
		client.handleMessage(msg);

		// confirm validity of message processing
		assertFalse(player.getIsKeyDown(GameKeys.JUMP));

	}
	
	
	/**
	 * actually an integration test
	 */
	@Test
	@Ignore
	public void testLatency(){
		MessageBundle ping = new MessageBundle();
		ping.setEntityId(UUID.fromString("9e05d450-78b6-11e3-981f-0800200c9a66"));
		ping.setCommand(Command.PING);
		ping.setParams("hello world");
		client.send(ping);
		long start = System.currentTimeMillis();
		MessageBundle pong;
		do{
			pong = client.receive();			
		}while(pong==null);

		long end = System.currentTimeMillis();
		assertEquals("9e05d450-78b6-11e3-981f-0800200c9a66",pong.getEntityId().toString());
		assertEquals("PONG",pong.getCommand().toString());
		assertEquals("hello world",pong.getParams()[0]);
		System.out.println("latency = "+(end-start)+"ms");
	}
	/**
	 * actually a stress test
	 */
	@Test
	@Ignore
	public void testStress(){
		for (int i = 0; i < 10000; i++) {
			MessageBundle ping = new MessageBundle();
			ping.setEntityId(UUID
					.fromString("9e05d450-78b6-11e3-981f-0800200c9a66"));
			ping.setCommand(Command.PING);
			ping.setParams("hello world");
			client.send(ping);
			long start = System.currentTimeMillis();
			MessageBundle pong;
			do {
				pong = client.receive();
			} while (pong == null);

			long end = System.currentTimeMillis();
			assertEquals("9e05d450-78b6-11e3-981f-0800200c9a66", pong
					.getEntityId().toString());
			assertEquals("PONG", pong.getCommand().toString());
			assertEquals("hello world", pong.getParams()[0]);
			System.out.println("latency = " + (end - start) + "ms");
		}
	}
	
}

package com.projecthawkthorne.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.State;
import com.projecthawkthorne.gamestate.Level;

public class ServerMessageHandlingTest {

	private static Server server;
	private float delta = 0.01f;

	@BeforeClass
	public static void setUpClass() throws Exception {
		HawkthorneGame.MODE = Mode.SERVER;
		server = Server.getSingleton();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServerPositionUpdate() {
		// create server-side player
		Player p = Player.getConnectedPlayer(UUID.randomUUID());
		p.x = 100;
		p.y = 200;
		p.velocityX = 300;
		p.velocityY = 400;
		p.setState(State.DEAD);

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.POSITIONVELOCITYUPDATE);
		msg.setEntityId(p.getId());
		msg.setParams("60000", "4.7", "-6.9", "-8", "JUMP");

		// process message
		server.handleMessage(msg);

		// verify correctness of message handling
		assertEquals(Float.parseFloat("60000"), p.x, delta);
		assertEquals(Float.parseFloat("4.7"), p.y, delta);
		assertEquals(Float.parseFloat("-6.9"), p.velocityX, delta);
		assertEquals(Float.parseFloat("-8"), p.velocityY, delta);
		assertEquals("JUMP", p.getState().toString());
	}

	@Test
	public void testPlayerRegistration() {
		// create unknown player
		UUID newId = UUID.fromString("ed5c9050-7629-11e3-981f-0800200c9a66");

		// create a received message
		MessageBundle msg = new MessageBundle();
		msg.setCommand(Command.REGISTERPLAYER);
		msg.setParams("VictorHugo", "town", "tavern");
		msg.setEntityId(newId);

		// process message
		server.handleMessage(msg);

		Player player = Player.getPlayerMap().get(msg.getEntityId());

		assertEquals("ed5c9050-7629-11e3-981f-0800200c9a66", player.getId()
				.toString());
		assertEquals("VictorHugo", player.getUsername());
		assertEquals("town", player.getLevel().getName());
		assertTrue(Level.get("town").getPlayers().contains(player));
		

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
		server.handleMessage(msg);

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
		server.handleMessage(msg);

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
		server.handleMessage(msg);

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
		server.handleMessage(msg);

		// confirm validity of message processing
		assertFalse(player.getIsKeyDown(GameKeys.JUMP));

	}

	@Test
	public void testLevelSwitching() throws InterruptedException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		LwjglApplication app = new LwjglApplication(new HawkGdxTest(Mode.SERVER){
			private Player player;

			@Override
			public Player getOutput() {
				return player;
			}

			@Override
			public void runTest() {
				// create known connected player
				UUID id = UUID.randomUUID();
				Player plyr = Player.getConnectedPlayer(id);

				// create a received message
				MessageBundle msg = new MessageBundle();
				msg.setCommand(Command.SWITCHLEVEL);
				msg.setEntityId(id);
				msg.setParams("town", "main");

				// process message
				server.handleMessage(msg);
				player = plyr;
			}
		}, config);

		HawkGdxTest listener = (HawkGdxTest) app.getApplicationListener();
		while (!listener.isComplete()) {
			Thread.sleep(100);
		}

		Player player = (Player) listener.getOutput();

		// confirm validity of message processing
		assertEquals("town", player.getLevel().getName());
		assertTrue(Level.getLevelMap().get("town").getPlayers()
				.contains(player));

		app.getApplicationListener().dispose();
		app.exit();

	}

}

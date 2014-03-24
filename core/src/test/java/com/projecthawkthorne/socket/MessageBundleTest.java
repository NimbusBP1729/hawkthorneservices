package com.projecthawkthorne.socket;

import java.net.DatagramPacket;

import junit.framework.TestCase;

import org.junit.Test;

import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.socket.udp.Command;
import com.projecthawkthorne.socket.udp.MessageBundle;
import com.projecthawkthorne.socket.udp.SocketUtils;

public class MessageBundleTest extends TestCase {
	DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testConvertBundleToStringWithoutParams() {
		MessageBundle mb = new MessageBundle();
		mb.setEntityId(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"));
		mb.setCommand(Command.REGISTERPLAYER);
		assertEquals("06017070-525b-40bc-88bf-ac6f7857d48c|REGISTERPLAYER",
				SocketUtils.bundleToString(mb));
	}

	@Test
	public void testConvertBundleToStringWithOneParameter() {
		MessageBundle mb = new MessageBundle();
		mb.setEntityId(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"));
		mb.setCommand(Command.REGISTERPLAYER);
		mb.setParams("param1");
		assertEquals(
				"06017070-525b-40bc-88bf-ac6f7857d48c|REGISTERPLAYER|param1",
				SocketUtils.bundleToString(mb));
	}

	@Test
	public void testConvertBundleToStringMultipleParameters() {
		MessageBundle mb = new MessageBundle();
		mb.setEntityId(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"));
		mb.setCommand(Command.REGISTERPLAYER);
		mb.setParams("param1", "param2", "param3");
		assertEquals(
				"06017070-525b-40bc-88bf-ac6f7857d48c|REGISTERPLAYER|param1,param2,param3",
				SocketUtils.bundleToString(mb));
	}

	@Test
	public void testConvertStringToBundle() {
		String msg = "06017070-525b-40bc-88bf-ac6f7857d48c|REGISTERPLAYER|myusername,levelName,param3";
		packet.setData(msg.getBytes());
		packet.setPort(27419);
		MessageBundle mb = SocketUtils.packetToBundle(packet);
		assertEquals(mb.getCommand(), Command.REGISTERPLAYER);
		UUID id = mb.getEntityId();
		assertEquals(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"),
				id);
		assertEquals(3, mb.getParams().length);
		assertEquals("myusername", mb.getParams()[0]);
		assertEquals("levelName", mb.getParams()[1]);
		assertEquals("param3", mb.getParams()[2]);
		assertEquals(27419, mb.getSocketAddress().getPort());
	}

	@Test
	public void testConvertSwitchStringToBundle() {
		String msg = "06017070-525b-40bc-88bf-ac6f7857d48c|SWITCHLEVEL|levelName,doorName";
		packet.setData(msg.getBytes());
		packet.setPort(27419);
		MessageBundle mb = SocketUtils.packetToBundle(packet);
		assertEquals(mb.getCommand(), Command.SWITCHLEVEL);
		UUID id = mb.getEntityId();
		assertEquals(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"),
				id);
		assertEquals(2, mb.getParams().length);
		assertEquals("levelName", mb.getParams()[0]);
		assertEquals("doorName", mb.getParams()[1]);
		assertEquals(27419, mb.getSocketAddress().getPort());
	}
	
//	@Test
//	@Ignore
//	public void testDirectionFromString(){
//		HawkthorneGame.MODE = Mode.CLIENT;
//		Player p = Player.getSingleton();
//		p.setIsKeyDown(GameKeys.DOWN, true);
//		p.setIsKeyDown(GameKeys.UP, true);
//		p.setIsKeyDown(GameKeys.LEFT, false);
//		p.setIsKeyDown(GameKeys.RIGHT, false);
//		
//		p.setDirectionsFromString("1010");
//		assertTrue(p.getIsKeyDown(GameKeys.DOWN));
//		assertFalse(p.getIsKeyDown(GameKeys.UP));
//		assertTrue(p.getIsKeyDown(GameKeys.LEFT));
//		assertFalse(p.getIsKeyDown(GameKeys.RIGHT));
//	}
//	
//	@Test
//	@Ignore
//	public void testStringFromDirection(){
//		
//
//		Player p = Player.getSingleton();
//		p.setIsKeyDown(GameKeys.DOWN, true);
//		p.setIsKeyDown(GameKeys.UP, true);
//		p.setIsKeyDown(GameKeys.LEFT, false);
//		p.setIsKeyDown(GameKeys.RIGHT, false);
//		assertEquals("1100",p.getDirectionsAsString());
//		
//	}
	
//	@Test
//	@Ignore
//	public void testIntegration() throws UnknownHostException {
//		HawkthorneGame.MODE = Mode.CLIENT;
//		MessageBundle mb = new MessageBundle();
//		mb.setCommand(Command.PING);
//		mb.setEntityId(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"));
//		mb.setParams("hello","world");
//		
//		Player p = Player.getSingleton();
//		p.registerPlayer(InetAddress.getByName("localhost"), 12345);
//		Client client = p.getClient();
//		client.send(mb);
//		
//		MessageBundle output = null;
//		do{
//			output = client.receive();
//		}while(output==null);
//		
//		assertEquals(Command.PONG, output.getCommand());
//		UUID id = mb.getEntityId();
//		assertEquals(UUID.fromString("06017070-525b-40bc-88bf-ac6f7857d48c"),
//				id);
//		assertEquals(2, output.getParams().length);
//		assertEquals("hello", output.getParams()[0]);
//		assertEquals("world", output.getParams()[1]);
//		assertEquals(12345, output.getSocketAddress().getPort());
//	}

	
}

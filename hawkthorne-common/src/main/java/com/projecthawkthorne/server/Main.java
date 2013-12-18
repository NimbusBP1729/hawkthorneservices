package com.projecthawkthorne.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;
import com.projecthawkthorne.gamestate.Levels;
import com.projecthawkthorne.server.nodes.Door;
import com.projecthawkthorne.server.nodes.Node;
import com.projecthawkthorne.socket.Server;
import com.projecthawkthorne.timer.Timer;

public class Main {
	private Server server;
	private Map<String, Player> players = new HashMap<String, Player>();
	public static Levels levels;

	public Main(int port) {
		super();
		System.out.println("starting the server");
		server = Server.createSingleton(port);
		levels = Levels.getSingleton();
	}

	public static void main(String[] args) {
		// LwjglApplication la = new LwjglApplication(new
		// Main(),"foo",800,600,true);
		int port = 12346;
		System.out.println("Using port " + port);
		Main m = new Main(port);
		while (true) {
			m.render();
		}
	}

	// Note to self 1: I should be monitoring (keyDown,timeStamp) pairs
	// by the client instead
	// Note to self 2: update responses to the client should send a bunch of
	// objects all at once, maybe?
	private void processBundle(DatagramPacket dp) {
		if (dp == null) {
			return;
		}
		byte[] msg = dp.getData();
		String[] tokens = new String(msg).split("\\s+", 3);
		String entity = tokens[0];
		String cmd = tokens[1];
		String params = tokens[2];
		params = params.trim();
		if ("keypressed".equals(cmd)) {
			params = params.split("\\s+")[0];
			Keys button = Keys.parse(params);
			Player player = players.get(entity);
			player.setKeyDown(button, true);
			player.keypressed(button);
		} else if ("keyreleased".equals(cmd)) {
			params = params.split("\\s+")[0];
			Keys button = Keys.parse(params);
			Player player = players.get(entity);
			player.getLevel();
			player.setKeyDown(button, false);
			player.keyreleased(button);
		} else if ("keydown".equals(cmd)) {
			// local button = parms:match("^(%S*)")
			// local level = players[entity].level
			// local player = players[entity]
		} else if ("update".equals(cmd)) {
			params = params.split("\\s+")[0];
			// sends an update back to the client
			Gamestate gs;
			gs = levels.get(params);
			Player player = players.get(entity);
			// update objects for client(s)
			if (gs instanceof Level) {
				Level level = (Level) gs;
				Iterator<Node> nit = level.getNodes().values().iterator();
				while (nit.hasNext()) {
					Node node = nit.next();
					// things to ignore
					// i.e, things the client doesn't need to draw
					if (node.type == null || node.type.equals("")
							|| node.type.equals("climbable")) {
						continue;
					}

					int port = dp.getPort();
					InetAddress ip = dp.getAddress();
					String reply;
					if (player.isNew() || player.updateList.contains(node)) {
						reply = node.getId() + " updateObject "
								+ node.packFull();
						player.updateList.remove(node);
						server.send(reply, ip, port);
					} else if (!node.type.equals("liquid")) {
						reply = node.getId() + " updateObject "
								+ node.packPartial();
						server.send(reply, ip, port);
					}

				}
				Iterator<Player> pit = level.getPlayers().iterator();
				while (pit.hasNext()) {
					Player otherPlayer = pit.next();
					String reply;
					if (player.isNew()) {
						reply = otherPlayer.id + " updatePlayer "
								+ otherPlayer.packFull();
					} else {
						reply = otherPlayer.id + " updatePlayer "
								+ otherPlayer.packPartial();
					}
					int port = dp.getPort();
					InetAddress ip = dp.getAddress();
					server.send(reply, ip, port);
				}
			}
			player.setIsNew(false);
		} else if ("register".equals(cmd)) {
			String[] chunks = params.split("\\s+");
			String name = chunks[0];
			String costume = chunks[1];
			String username = entity;
			if (chunks.length >= 3) {
				username = chunks[2];
			}
			System.out.println("registering a new player:" + entity);
			System.out.println("msg_or_ip:" + dp.getAddress());
			System.out.println("port_or_nil:" + dp.getPort());
			// TODO: if entity # is already taken pick another and tell
			// the client
			Gamestate overworld = levels.get("overworld");
			Player player = new Player(entity, dp.getAddress(), dp.getPort(),
					overworld);
			player.setUsername(username);
			player.getCharacter().setName(name);
			player.getCharacter().setCostume(costume);
			players.put(entity, player);
		} else if ("changeCostume".equals(cmd)) {
			String[] chunks = params.split("\\s+");
			String name = chunks[0];
			String costume = chunks[1];
			Player player = players.get(entity);
			player.getCharacter().setName(name);
			player.getCharacter().setCostume(costume);
		} else if ("enterLevel".equals(cmd)) {
			throw new UnsupportedOperationException("enterLevel is deprecated");
		} else if ("enter".equals(cmd)) {
			String[] chunks = params.split("\\s+");
			Gamestate level = levels.get(chunks[0]);
			// Door door = level.getDoor(chunks[1]);
			// temporary
			Door door = level.getDoor("main");
			Player player = players.get(entity);
			if (player == null) {
				System.err.println("player was not found. Inserting.");
				player = new Player(entity, dp.getAddress(), dp.getPort(),
						level);
				players.put(entity, player);
			}
			Levels.switchState(level, door, player, false);
		} else if ("unregister".equals(cmd)) {
			System.out.println("unregistering a player:" + entity);
			System.out.println("msg_or_ip:" + dp.getAddress());
			System.out.println("port_or_nil:" + dp.getPort());
			players.remove(entity);
		} else if ("quit".equals(cmd)) {
			boolean running = false;
		} else {
			new Exception("unrecognized command: " + cmd).printStackTrace();
		}
	}

	public void render() {
		Timer.updateTimers();
		Iterator<Gamestate> it = levels.values().iterator();

		Gamestate level;
		// update all the server-side levels
		while (it.hasNext()) {
			level = it.next();
			level.update();
		}

		// receive a new bundle
		DatagramPacket receivedPacket = server.receive();

		// TODO: investigate. we shouldn't need to give up.
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		while (receivedPacket != null && elapsedTime < 1000) {
			// process bundle if necessary
			processBundle(receivedPacket);
			receivedPacket = server.receive();
			elapsedTime = System.currentTimeMillis() - startTime;
		}
		// TODO Auto-generated method stub
		// Level level = this.levels.get("town");
		// if(level!=null){
		// level.drawStuff();
		// }
		// //g.drawString("Hello World!!!", 100, 100);

	}

}
package com.projecthawkthorne.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.State;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;

public class Server {

	private static final int DATA_SIZE = 300;
	private DatagramSocket serverSocket;
	private byte[] receiveData = new byte[DATA_SIZE];
	private byte[] sendData = new byte[DATA_SIZE];
	private static Server singleton = null;
	private DatagramPacket receivePacket = new DatagramPacket(receiveData,
			receiveData.length);
	private DatagramPacket sendPacket = new DatagramPacket(sendData,
			sendData.length);
	private MessageBundle response = new MessageBundle();
	public static Map<UUID, InetSocketAddress> addressMap = new HashMap<UUID, InetSocketAddress>();

	private Server() throws SocketException {
		int port;
		try {
			port = Integer.valueOf(System.getenv("HAWK_PORT"));
		} catch (Exception e) {
			port = 12345;
		}
		Gdx.app.log("server", "creating server at port:" + port);
		try {
			Gdx.app.log("server", "creating server at address:" + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			Gdx.app.error("server", "error resolving address");
		}


		serverSocket = new DatagramSocket(port);
		serverSocket.setSoTimeout(17);// ~1/60 seconds

	}

	/**
	 * returns one identical server every time
	 * 
	 * @return the server
	 * @throws SocketException 
	 */
	public static Server getSingleton() throws SocketException {
		if (singleton == null) {
			singleton = new Server();
		}
		if (HawkthorneGame.MODE != Mode.SERVER) {
			throw new UnsupportedOperationException(
					"must be a server to use this method");
		}

		return singleton;
	}

	/**
	 * receives a UDP datagram from a client
	 * 
	 * @return the datagram packet
	 */
	public DatagramPacket receiveRaw() {

		try {
			SocketUtils.clearPacket(receivePacket);
			serverSocket.receive(receivePacket);
			return receivePacket;
		} catch (SocketTimeoutException ex) {
		} catch (IOException ex) {
			Gdx.app.error("server", ex.getMessage(), ex);
		}
		return null;
	}

	public MessageBundle receive() {
		return SocketUtils.packetToBundle(receiveRaw());
	}

	/**
	 * 
	 * @param message
	 *            formatted message to send to the client
	 * @param ip
	 *            the IP address of the recipient '*' indicates all connected
	 *            clients
	 * @param port
	 *            the port of the recipient
	 * @return true if the message was sent
	 * @throws IOException
	 */
	private boolean sendRaw(String message, InetAddress ip, int port) {
		SocketUtils.clearPacket(sendPacket);
		sendData = message.getBytes();
		sendPacket.setData(sendData);
		sendPacket.setAddress(ip);
		sendPacket.setPort(port);
		Gdx.app.debug("server",
				"TO CLIENT: " + message + "\n" + "   socket: " + ip + ","
						+ port + "\n" + "     time: "
						+ System.currentTimeMillis() + "\n");
		try {
			serverSocket.send(sendPacket);
		} catch (IOException ex) {
			Gdx.app.error("server", ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public boolean send(MessageBundle mb, InetAddress ip, int port) {
		return sendRaw(SocketUtils.bundleToString(mb), ip, port);
	}

	public int sendToAllExcept(MessageBundle msg, UUID excludedId) {
		int count = 0;
		for (UUID id : addressMap.keySet()) {
			if (!(id.equals(excludedId))) {
				InetAddress address = addressMap.get(id).getAddress();
				int port = addressMap.get(id).getPort();
				this.send(msg, address, port);
				count++;
			}
		}
		return count;
	}

	/**
	 * send message to connected clients
	 * 
	 * @param message
	 * @return amount of clients sent to
	 */
	public int sendToAll(MessageBundle mb) {
		return this.sendToAllExcept(mb, null);
	}

	public void handleMessage(MessageBundle msg) {
		if (msg == null) {
			return;
		}

		if (msg.getCommand() == Command.REGISTERPLAYER) {
			UUID id = msg.getEntityId();
			addressMap.put(id, msg.getSocketAddress());
			Player newPlayer = Player.getConnectedPlayer(id);
			newPlayer.setUsername(msg.getParams()[0]);
			
			Level newLevel = Level.get(msg.getParams()[1]);
			Door door = newLevel.getDoor(msg.getParams()[2]);
			Level.switchState(newLevel, door, newPlayer);

			//tell everyone about the new kid
			this.sendToAllExcept(msg, id);
			
			//tell the new kid about everyone
			for(Player p: Player.getPlayerMap().values()){
				InetSocketAddress sockAddr = msg.getSocketAddress();
				MessageBundle mb = new MessageBundle();
				mb.setCommand(Command.REGISTERPLAYER);
				mb.setParams(p.getUsername(),HawkthorneGame.START_LEVEL,"main");
				mb.setEntityId(p.getId());
				this.send(mb, sockAddr.getAddress(), sockAddr.getPort());
			}
		} else if (msg.getCommand() == Command.SWITCHLEVEL) {
			UUID id = msg.getEntityId();
			Gamestate newLevel = Level.get(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			Door door = newLevel.getDoor(msg.getParams()[1]);
			Level.switchState(newLevel, door, player);
			//tell everyone else you moved
			this.sendToAllExcept(msg, id);
		} else if (msg.getCommand() == Command.KEYPRESSED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, true);
			player.keypressed(gk);
			this.sendToAllExcept(msg, id);
		} else if (msg.getCommand() == Command.KEYRELEASED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, false);
			player.keyreleased(gk);
			this.sendToAllExcept(msg, id);
		} else if (msg.getCommand() == Command.POSITIONVELOCITYUPDATE) {
			Player p = Player.getConnectedPlayer(msg.getEntityId());
			float factor = 0.0f;
			p.x = SocketUtils.lerp(Float.parseFloat(msg.getParams()[0]), p.x,
					factor);
			p.y = SocketUtils.lerp(Float.parseFloat(msg.getParams()[1]), p.y,
					factor);
			p.velocityX = SocketUtils.lerp(
					Float.parseFloat(msg.getParams()[2]), p.velocityX, factor);
			p.velocityY = SocketUtils.lerp(
					Float.parseFloat(msg.getParams()[3]), p.velocityY, factor);
			p.setState(State.valueOf(msg.getParams()[4]));
			p.setDirectionsFromString(msg.getParams()[5]);
			p.moveBoundingBox();
			this.sendToAllExcept(msg, p.getId());
		} else if (msg.getCommand() == Command.PING) {
			response.setCommand(Command.PONG);
			response.setEntityId(msg.getEntityId());
			response.setParams(msg.getParams());
			InetSocketAddress sockAddr = msg.getSocketAddress();
			this.send(response, sockAddr.getAddress(), sockAddr.getPort());
		} else if (msg.getCommand() == Command.GETPLAYERCOUNT) {
			response.setCommand(Command.SENDPLAYERCOUNT);
			response.setEntityId(msg.getEntityId());
			response.setParams(String.valueOf(Player.getPlayerMap().size()));
			InetSocketAddress sockAddr = msg.getSocketAddress();
			this.send(response, sockAddr.getAddress(), sockAddr.getPort());
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
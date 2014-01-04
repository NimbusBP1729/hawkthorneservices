package com.projecthawkthorne.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Levels;

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
	Logger log = Logger.getLogger(this.getClass().getName());
	private Set<InetSocketAddress> addressSet = new HashSet<InetSocketAddress>();

	private Server(int port) {
		try {
			serverSocket = new DatagramSocket(port);
			serverSocket.setSoTimeout(17);// ~1/60 seconds

		} catch (SocketException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}

	}

	/**
	 * returns one identical server every time
	 * 
	 * @return the server
	 */
	public static Server getSingleton() {
		if (HawkthorneGame.MODE != Mode.SERVER) {
			throw new UnsupportedOperationException(
					"must be a server to use this method");
		}

		if (singleton == null) {
			singleton = new Server(12345);
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
			InetSocketAddress socketAddress = (InetSocketAddress) receivePacket
					.getSocketAddress();
			addressSet.add(socketAddress);

			log.log(Level.INFO,
					"FROM CLIENT: " + new String(receivePacket.getData())
							+ "\n" + "     socket: "
							+ socketAddress.getAddress() + ","
							+ socketAddress.getPort() + "\n" + "       time: "
							+ System.currentTimeMillis());

			return receivePacket;
		} catch (SocketTimeoutException ex) {
		} catch (IOException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
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
		log.log(Level.INFO, "TO CLIENT: " + message);
		log.log(Level.INFO, "   socket: " + ip + "," + port + "\n");
		log.log(Level.INFO, "     time: " + System.currentTimeMillis() + "\n");
		try {
			serverSocket.send(sendPacket);
		} catch (IOException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public boolean send(MessageBundle mb, InetAddress ip, int port) {
		return sendRaw(SocketUtils.bundleToString(mb), ip, port);
	}

	/**
	 * send message to connected clients
	 * 
	 * @param message
	 * @return amount of clients sent to
	 */
	public int sendToAll(MessageBundle mb) {
		int count = 0;
		for (InetSocketAddress addr : addressSet) {
			if (this.send(mb, addr.getAddress(), addr.getPort())) {
				count++;
			}
		}
		return count;
	}

	public void handleMessage(MessageBundle msg) {
		if (msg == null) {
			return;
		}

		if (msg.getCommand() == Command.REGISTERPLAYER) {
			UUID id = msg.getEntityId();
			Player.getConnectedPlayer(id);
		} else if (msg.getCommand() == Command.SWITCHLEVEL) {
			UUID id = msg.getEntityId();
			Gamestate newLevel = Levels.getSingleton().get(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			Door door = newLevel.getDoor(msg.getParams()[1]);
			Levels.switchState(newLevel, door, player);
		} else if (msg.getCommand() == Command.KEYPRESSED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0].trim());
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, true);
			player.keypressed(gk);
		} else if (msg.getCommand() == Command.KEYRELEASED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0].trim());
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, false);
			player.keyreleased(gk);
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
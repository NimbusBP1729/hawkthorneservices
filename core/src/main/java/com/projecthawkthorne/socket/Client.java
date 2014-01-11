package com.projecthawkthorne.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Logger;

import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.Mode;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.State;
import com.projecthawkthorne.gamestate.Gamestate;
import com.projecthawkthorne.gamestate.Level;

public class Client {

	private DatagramSocket clientSocket;
	private byte[] receiveData = new byte[1024];
	private DatagramPacket receivePacket = new DatagramPacket(receiveData,
			receiveData.length);
	private byte[] sendData = new byte[1024];
	private DatagramPacket sendPacket;

	public int serverPort;
	public InetAddress serverIp;
	private static Client singleton;
	private Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * private because this is invoked as a singleton
	 * 
	 * @param port
	 */
	private Client() {
		try {
			this.clientSocket = new DatagramSocket();
			this.clientSocket.setSoTimeout(17);// ~1/60 seconds

			try {
				this.serverPort = Integer.valueOf(System.getenv("HAWK_PORT"));
			} catch (Exception e) {
				this.serverPort = 12345;
			}
			try {
				this.serverIp = InetAddress.getByName(System
						.getenv("HAWK_ADDRESS"));
			} catch (Exception e) {
				this.serverIp = InetAddress.getLocalHost();
			}
			log.setLevel(java.util.logging.Level.WARNING);

			System.out.println("Connecting to server at address:port==" + this.serverIp.getHostAddress() + ":"+ this.serverPort);
			sendPacket = new DatagramPacket(sendData, sendData.length,
					this.serverIp, this.serverPort);

		} catch (SocketException ex) {
			log.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
		} catch (UnknownHostException ex) {
			log.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
		}
	}

	/**
	 * returns one identical server every time
	 * 
	 * @return the client
	 */
	public static Client getSingleton() {
		if (HawkthorneGame.MODE != Mode.CLIENT) {
			throw new UnsupportedOperationException(
					"must be a client to use this method");
		}
		if (singleton == null) {
			singleton = new Client();
		}
		return singleton;
	}

	/**
	 * receives a UDP datagram from a server
	 * 
	 * @return datagram or null if none are found
	 */
	public DatagramPacket receiveRaw() {
		try {
			SocketUtils.clearPacket(receivePacket);
			clientSocket.receive(receivePacket);
			return receivePacket;
		} catch (SocketTimeoutException e) {
		} catch (Exception e) {
			log.log(java.util.logging.Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public MessageBundle receive() {
		return SocketUtils.packetToBundle(receiveRaw());
	}

	/**
	 * send a message in the following format: (entity) (cmd) (params)
	 * 
	 * @param message
	 *            formatted message to send to the server
	 * @return true if the message was sent
	 * @throws IOException
	 */
	private boolean sendRaw(String message) {
		try {
			SocketUtils.clearPacket(sendPacket);
			sendData = message.getBytes();
			sendPacket.setData(sendData);
			clientSocket.send(sendPacket);
			log.log(java.util.logging.Level.INFO, "TO SERVER: " + message);
			log.log(java.util.logging.Level.INFO, "     time: " + System.currentTimeMillis());
			return true;
		} catch (Exception e) {
			log.log(java.util.logging.Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	public boolean send(MessageBundle mb) {
		return sendRaw(SocketUtils.bundleToString(mb));
	}

	public void handleMessage(MessageBundle msg) {
		if (msg == null) {
			return;
		} else if (msg.getCommand() == Command.POSITIONVELOCITYUPDATE) {
			Player p = Player.getConnectedPlayer(msg.getEntityId());
			float factor = 0.5f;
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
		} else if (msg.getCommand() == Command.REGISTERPLAYER) {
			Player p = Player.getConnectedPlayer(msg.getEntityId());
			p.setUsername(msg.getParams()[0]);
			Level newLevel = Level.get(msg.getParams()[1]);
			Door door = newLevel.getDoor(msg.getParams()[2]);
			Level.switchState(newLevel, door, p);
		} else if (msg.getCommand() == Command.SWITCHLEVEL) {
			UUID id = msg.getEntityId();
			Gamestate newLevel = Level.get(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			Door door = newLevel.getDoor(msg.getParams()[1]);
			Level.switchState(newLevel, door, player);
		} else if (msg.getCommand() == Command.KEYPRESSED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, true);
			player.keypressed(gk);
		} else if (msg.getCommand() == Command.KEYRELEASED) {
			UUID id = msg.getEntityId();
			GameKeys gk = GameKeys.valueOf(msg.getParams()[0]);
			Player player = Player.getConnectedPlayer(id);
			player.setIsKeyDown(gk, false);
			player.keyreleased(gk);
		} else if (msg.getCommand() == Command.PONG) {
			UUID id = msg.getEntityId();
			long end = System.currentTimeMillis();
			try{
				long start = Long.valueOf(msg.getParams()[0]);
				System.out.println("latency = " + (end-start)+"ms");
				System.out.println("=======================");
			}catch(Exception e){
				System.err.println("error reporting latency");
				e.printStackTrace();
			}
		}  else {
			throw new UnsupportedOperationException(msg.getCommand().toString());
		}
	}
}
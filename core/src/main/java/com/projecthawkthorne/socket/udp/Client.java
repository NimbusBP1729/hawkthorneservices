package com.projecthawkthorne.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.badlogic.gdx.Gdx;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
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

	private Client(InetAddress serverIp, int serverPort, int timeout) {
		try {
			this.clientSocket = new DatagramSocket();
			this.clientSocket.setSoTimeout(timeout);// ~1/60 seconds

			this.serverPort = serverPort;
			this.serverIp = serverIp;
			Gdx.app.log("client", "Connecting to server at address:port==" + this.serverIp.getHostAddress() + ":"+ this.serverPort);
			sendPacket = new DatagramPacket(sendData, sendData.length,
					this.serverIp, this.serverPort);

		} catch (SocketException ex) {
			Gdx.app.error("client", ex.getMessage());
		}
	}
	
	
	
	/**
	 * 
	 * @param port
	 */
	public Client(InetAddress serverIp, int serverPort) {
		this(serverIp, serverPort, 17);
	}
	
	public static Client getNewQueryClient(InetAddress serverIp, int serverPort){
		return new Client(serverIp, serverPort, 1000);
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
			Gdx.app.error("client", e.getMessage());
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
			Gdx.app.debug("client", "TO SERVER: " + message);
			Gdx.app.debug("client", "     time: " + System.currentTimeMillis());
			return true;
		} catch (Exception e) {
			Gdx.app.error("client", e.getMessage());
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
		}  else {
			throw new UnsupportedOperationException(msg.getCommand().toString());
		}
	}
}
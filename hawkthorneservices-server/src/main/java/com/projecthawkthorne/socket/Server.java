package com.projecthawkthorne.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		if (singleton == null) {
			singleton = new Server(12346);
		}
		return singleton;
	}

	/**
	 * receives a UDP datagram from a client
	 * 
	 * @return the datagram packet
	 */
	public DatagramPacket receive() {

		try {
			clearPacket(receivePacket);
			serverSocket.receive(receivePacket);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			log.log(Level.INFO, "FROM CLIENT: " + receivePacket.getData());
			log.log(Level.INFO, "     socket: " + IPAddress + "," + port + "\n");
			log.log(Level.INFO, "       time: " + System.currentTimeMillis());

			return receivePacket;
		} catch (SocketTimeoutException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		} catch (IOException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return null;
	}

	private void clearPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		for (int i = 0; i < data.length; i++) {
			data[i] = '\0';
		}
		packet.setAddress(null);
		packet.setPort(-1);
		packet.setSocketAddress(null);
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
	public boolean send(String message, InetAddress ip, int port) {
		// temporary
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

	// TODO: replace this stub to broadcast messages
	/**
	 * send message to connected clients
	 * 
	 * @param message
	 * @return amount of clients sent to
	 */
	public int send(String message) {
		throw new UnsupportedOperationException();
	}

}
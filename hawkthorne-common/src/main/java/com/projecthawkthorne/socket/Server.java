package com.projecthawkthorne.socket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final int DATA_SIZE = 300;
	public static boolean DEBUG = false;
	private DatagramSocket serverSocket;
	// private LocalComm serverSocket;
	private byte[] receiveData = new byte[DATA_SIZE];
	private byte[] sendData = new byte[DATA_SIZE];
	private static Server singleton = null;
	private DatagramPacket receivePacket = new DatagramPacket(receiveData,
			receiveData.length);
	private DatagramPacket sendPacket = new DatagramPacket(sendData,
			sendData.length);
	private BufferedWriter logFile;

	private Server(int port) {
		try {
			serverSocket = new DatagramSocket(port);
			// serverSocket = new LocalComm(Type.SERVER);
			serverSocket.setSoTimeout(17);// ~1/60 seconds

		} catch (SocketException ex) {
			Logger.getLogger(Server.class.getName())
					.log(Level.SEVERE, null, ex);
		}

		DateFormat df = new SimpleDateFormat("yyyy_MM_dd");

		new File("logs").mkdir();
		String prefix = "logs/server" + df.format(new Date());
		String suffix = ".log";

		File f = new File(prefix + suffix);
		int i = 1;
		while (f.exists()) {
			f = new File(prefix + "_" + i + suffix);
			i++;
		}
		try {
			logFile = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			e.printStackTrace();
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

	public static Server createSingleton(int port) {
		singleton = new Server(port);
		return singleton;
	}

	/**
	 * receives a UDP datagram from a client
	 * 
	 * @return the datagram packet
	 */
	public DatagramPacket receive() {
		// temporary

		try {
			clearPacket(receivePacket);
			serverSocket.receive(receivePacket);
			// byte[] sentence = receivePacket.getData();
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			if (Server.DEBUG) {
				logFile.write("FROM CLIENT: " + receivePacket.getData() + "\n");
				logFile.write("     socket: " + IPAddress + "," + port + "\n");
				logFile.write("       time: " + System.currentTimeMillis()
						+ "\n");
				logFile.flush();
			}

			return receivePacket;
		} catch (SocketTimeoutException ex) {
			// Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
			// ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName())
					.log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private void clearPacket(DatagramPacket receivePacket2) {
		byte[] data = receivePacket.getData();
		for (int i = 0; i < data.length; i++) {
			data[i] = '\0';
		}
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
		try {
			if (Server.DEBUG) {
				logFile.write("TO CLIENT: " + message + "\n");
				logFile.write("   socket: " + ip + "," + port + "\n");
				logFile.write("     time: " + System.currentTimeMillis() + "\n");
				logFile.flush();
			}
			serverSocket.send(sendPacket);
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName())
					.log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

	// TODO: replace this stub to broadcast messages
	/**
	 * send message to all connected clients
	 * 
	 * @param message
	 * @return amount of clients sent to
	 */
	public int send(String message) {
		return 0;
	}

}
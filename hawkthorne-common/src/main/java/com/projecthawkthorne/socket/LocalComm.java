package com.projecthawkthorne.socket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;

import com.projecthawkthorne.datastructures.Queue;

public class LocalComm {
	public static enum Type {
		SERVER, CLIENT
	};

	private static LocalComm server;
	private static LocalComm client;
	private static int clientCount = 0;

	private final Type type;
	private InetAddress ip;
	private int port;
	private Queue<DatagramPacket> messages = new Queue<DatagramPacket>();
	private long timeOut;

	public LocalComm(Type commType) {
		if (commType.equals(Type.SERVER)) {
			type = Type.SERVER;
			server = this;
		} else if (commType.equals(Type.CLIENT)) {
			type = Type.CLIENT;
			client = this;
		} else {
			throw new UnsupportedOperationException("Type: " + commType
					+ " disallowed for a communicator");
		}

	}

	public void setSoTimeout(long i) throws SocketException {
		this.timeOut = i;
	}

	public void receive(DatagramPacket receivePacket)
			throws SocketTimeoutException {
		long start = System.currentTimeMillis();
		receivePacket.setData(new byte[1024]);
		DatagramPacket r = null;
		long curTime = System.currentTimeMillis();
		while (r == null && curTime - start < timeOut) {
			try {
				r = this.messages.pop();
			} catch (NoSuchElementException e) {
			}
			curTime = System.currentTimeMillis();
		}
		if (curTime - start >= timeOut) {
			throw new SocketTimeoutException();
		}
		receivePacket.setAddress(r.getAddress());
		receivePacket.setData(r.getData(), r.getOffset(), r.getLength());
		receivePacket.setPort(r.getPort());
		receivePacket.setSocketAddress(r.getSocketAddress());
		return;
	}

	public void send(DatagramPacket sendPacket) {
		if (this.type == Type.CLIENT) {
			server.messages.push(sendPacket);
		} else {
			// Note: I don't currently support multiple clients
			client.messages.push(sendPacket);
		}
	}
}

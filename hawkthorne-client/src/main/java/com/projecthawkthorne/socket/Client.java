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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.client.display.Character;
import com.projecthawkthorne.client.display.Node;
import com.projecthawkthorne.client.display.Player;

public class Client {

	private DatagramSocket clientSocket;
	// private LocalComm clientSocket;
	private String entity;
	public HashMap<String, Player> players;
	// private HashMap<String, Character> characters;
	public HashMap<String, HashMap<String, Node>> world;

	// TODO: create overworld
	private String level = HawkthorneGame.START_LEVEL;

	private byte[] receiveData;
	private DatagramPacket receivePacket;
	private byte[] sendData;
	private DatagramPacket sendPacket;

	public static int serverPort;
	public static InetAddress serverIp;
	private static Client singleton;
	public static Character clientCharacter;
	private static BufferedWriter logFile;

	public HashMap<Integer, Boolean> keyDown = new HashMap<Integer, Boolean>();

	/**
	 * private because this is invoked as a singleton
	 * 
	 * @param port
	 */
	private Client() {
		try {
			this.clientSocket = new DatagramSocket();
			// this.clientSocket = new LocalComm(Type.CLIENT);
			this.clientSocket.setSoTimeout(17);// ~1/60 seconds

			System.out.println("Using port:" + Client.serverPort);
			System.out.println("Using address:" + Client.serverIp);
			this.entity = ("player" + Math.round(Math.random() * (99999)));
			this.players = new HashMap<String, Player>();
			// this.characters = new HashMap<String,Character>();
			this.world = new HashMap<String, HashMap<String, Node>>();
			receiveData = new byte[1024];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			sendData = new byte[1024];
			sendPacket = new DatagramPacket(sendData, sendData.length,
					Client.serverIp, Client.serverPort);

			DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
			String prefix = "client" + df.format(new Date());
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

			initKeys();

			clientCharacter = new Character();
			String message = this.getEntity() + " register "
					+ clientCharacter.name + " " + clientCharacter.costume;
			this.send(message);
			this.players.put(this.entity, new Player(clientCharacter.name,
					clientCharacter.costume));

			// Client should only assign his first level by himself, the rest
			// this.level = HawkthorneGame.START_LEVEL;
			this.world.put(this.level, new HashMap<String, Node>());
			// YIKES... which do i want
			message = this.getEntity() + " enter " + level;
			this.send(message);
			message = this.getEntity() + " update " + level;
			this.send(message);

		} catch (SocketException ex) {
			Logger.getLogger(Client.class.getName())
					.log(Level.SEVERE, null, ex);
		}
	}

	private void initKeys() {
		keyDown.put(Keys.LEFT, false);
		keyDown.put(Keys.RIGHT, false);
		keyDown.put(Keys.UP, false);
		keyDown.put(Keys.DOWN, false);
		keyDown.put(Keys.D, false);
		keyDown.put(Keys.ESCAPE, false);
		keyDown.put(Keys.X, false);
		keyDown.put(Keys.C, false);
		keyDown.put(Keys.V, false);
	}

	/**
	 * returns one identical server every time
	 * 
	 * @return the client
	 */
	public static Client getSingleton() {
		return singleton;
	}

	/**
	 * receives a UDP datagram from a server
	 * 
	 * @return datagram or null if none are found
	 */
	public DatagramPacket receive() {
		try {
			clearPacket(receivePacket);
			clientSocket.receive(receivePacket);
			logFile.write("FROM SERVER: '"
					+ new String(receivePacket.getData()).trim() + "'\n");
			logFile.write("    address: '" + receivePacket.getAddress() + "'\n");
			logFile.write("       port: '" + receivePacket.getPort() + "'\n");
			logFile.write("       time: '" + System.currentTimeMillis() + "'\n");
			logFile.flush();
			// clientSocket.close();

			return receivePacket;
		} catch (SocketTimeoutException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private void clearPacket(DatagramPacket receivePacket2) {
		// TODO Auto-generated method stub
		byte[] data = receivePacket.getData();
		for (int i = 0; i < data.length; i++) {
			data[i] = '\0';
		}
	}

	/**
	 * send a message in the following format: (entity) (cmd) (params)
	 * 
	 * @param message
	 *            formatted message to send to the server
	 * @return true if the message was sent
	 * @throws IOException
	 */
	public boolean send(String message) {
		try {
			sendData = message.getBytes();
			sendPacket.setData(sendData);
			// = new DatagramPacket(sendData, sendData.length, Client.serverIp,
			// Client.serverPort);
			clientSocket.send(sendPacket);
			logFile.write("TO SERVER: '" + message + "'\n");
			logFile.write("     time: '" + System.currentTimeMillis() + "'\n");
			logFile.flush();
			// clientSocket.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getEntity() {
		return entity;
	}

	public void draw(SpriteBatch batch, OrthographicCamera cam) {

		if (this.world == null) {
			throw new NullPointerException("world can't be null");
		}
		if (this.level == null) {
			throw new NullPointerException("level can't be null");
		}
		if (this.world.get(this.level) == null) {
			System.err.println("world has no level by the name: " + this.level
					+ "... initializing");
			this.world.put(this.level, new HashMap<String, Node>());
		}
		Iterator<Node> nit = this.world.get(this.level).values().iterator();

		long curTime = System.currentTimeMillis();
		List<Node> liquids = new LinkedList<Node>();
		while (nit.hasNext()) {
			Node n = nit.next();
			if (n.vertices != null) {
				// batch.end();
				// n.drawAsPolygon(cam);
				// batch.begin();
			} else if (n.getType() == null && n.getSheetPath() != null) {
				n.drawAsSprite(batch);
			} else if (n.getType().equals("liquid")) {
				liquids.add(n);
			} else if (curTime - n.getLastUpdate() < 1000) {
				n.draw(batch);
			} else {
				// TODO: find a threadsafe removal strategy
			}
		}

		Iterator<Player> pit = this.players.values().iterator();
		while (pit.hasNext()) {
			Player p = pit.next();
			if (p == null) {
				System.err.println("player is null");
			} else if (p.levelName == null) {
				System.err.println("player's level is null");
			}
			if (this.level.equals(p.levelName)) {
				p.draw(batch);
			}
		}
		for (int i = 0; i < liquids.size(); i++) {
			liquids.get(i).draw(batch);
		}
	}

	public void update() {
		// TODO Auto-generated method stub
		String message = String
				.format("%s %s %s", entity, "update", this.level);
		this.send(message);

		updateKeys();

	}

	/**
	 * sends messages to the server indicating a new keypress or release
	 */
	private void updateKeys() {
		// TODO Auto-generated method stub

		String message;
		if (keyDown.get(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.LEFT)) {
			keyDown.put(Keys.LEFT, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"LEFT");
			this.send(message);
		} else if (!keyDown.get(Keys.LEFT) && Gdx.input.isKeyPressed(Keys.LEFT)) {
			keyDown.put(Keys.LEFT, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"LEFT");
			this.send(message);
		}

		if (keyDown.get(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.RIGHT)) {
			keyDown.put(Keys.RIGHT, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"RIGHT");
			this.send(message);
		} else if (!keyDown.get(Keys.RIGHT)
				&& Gdx.input.isKeyPressed(Keys.RIGHT)) {
			keyDown.put(Keys.RIGHT, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"RIGHT");
			this.send(message);
		}

		if (keyDown.get(Keys.UP) && !Gdx.input.isKeyPressed(Keys.UP)) {
			keyDown.put(Keys.UP, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"UP");
			this.send(message);
		} else if (!keyDown.get(Keys.UP) && Gdx.input.isKeyPressed(Keys.UP)) {
			keyDown.put(Keys.UP, true);
			message = String
					.format("%s %s %s", this.entity, "keypressed", "UP");
			this.send(message);
		}

		if (keyDown.get(Keys.DOWN) && !Gdx.input.isKeyPressed(Keys.DOWN)) {
			keyDown.put(Keys.DOWN, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"DOWN");
			this.send(message);
		} else if (!keyDown.get(Keys.DOWN) && Gdx.input.isKeyPressed(Keys.DOWN)) {
			keyDown.put(Keys.DOWN, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"DOWN");
			this.send(message);
		}

		if (keyDown.get(Keys.X) && !Gdx.input.isKeyPressed(Keys.X)) {
			keyDown.put(Keys.X, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"JUMP");
			this.send(message);
		} else if (!keyDown.get(Keys.X) && Gdx.input.isKeyPressed(Keys.X)) {
			keyDown.put(Keys.X, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"JUMP");
			this.send(message);
		}

		if (keyDown.get(Keys.ESCAPE) && !Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			keyDown.put(Keys.ESCAPE, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"START");
			this.send(message);
		} else if (!keyDown.get(Keys.ESCAPE)
				&& Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			keyDown.put(Keys.ESCAPE, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"START");
			this.send(message);
		}

		if (keyDown.get(Keys.D) && !Gdx.input.isKeyPressed(Keys.D)) {
			keyDown.put(Keys.D, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"SELECT");
			this.send(message);
		} else if (!keyDown.get(Keys.D) && Gdx.input.isKeyPressed(Keys.D)) {
			keyDown.put(Keys.D, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"SELECT");
			this.send(message);
		}

		if (keyDown.get(Keys.C) && !Gdx.input.isKeyPressed(Keys.C)) {
			keyDown.put(Keys.C, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"ATTACK");
			this.send(message);
		} else if (!keyDown.get(Keys.C) && Gdx.input.isKeyPressed(Keys.C)) {
			keyDown.put(Keys.C, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"ATTACK");
			this.send(message);
		}

		if (keyDown.get(Keys.V) && !Gdx.input.isKeyPressed(Keys.V)) {
			keyDown.put(Keys.V, false);
			message = String.format("%s %s %s", this.entity, "keyreleased",
					"INTERACT");
			this.send(message);
		} else if (!keyDown.get(Keys.V) && Gdx.input.isKeyPressed(Keys.V)) {
			keyDown.put(Keys.V, true);
			message = String.format("%s %s %s", this.entity, "keypressed",
					"INTERACT");
			this.send(message);
		}
	}

	public void setLevel(String toLevel) {
		// TODO Auto-generated method stub
		this.level = toLevel;
	}

	public static void init() {
		Client.singleton = new Client();
	}
}
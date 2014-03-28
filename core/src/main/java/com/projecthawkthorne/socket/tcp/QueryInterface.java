package com.projecthawkthorne.socket.tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.socket.udp.Client;
import com.projecthawkthorne.socket.udp.Command;
import com.projecthawkthorne.socket.udp.MessageBundle;

//mockup class for all queries
public class QueryInterface {

	private String url = "http://54.221.47.242/hawk/";
	private Json json;
	private HawkthorneGame game;

	public QueryInterface(HawkthorneGame game) {
		this.game = game;
		this.json = new Json();
		this.json.setOutputType(OutputType.json);
	}

	public void getServerList(final Results results,
			final List<List<String>> table) throws UnknownHostException {
		final MessageBundle pingMsg = new MessageBundle();
		final InetAddress ip = InetAddress.getByName("255.255.255.255");

		table.clear();
		int port;
		try {
			port = Integer.valueOf(System.getenv("HAWK_PORT"));
		} catch (Exception e) {
			port = 12345;
		}
		final Client client;
		client = Client.getNewQueryClient(ip,port);

		pingMsg.setCommand(Command.GETPLAYERCOUNT);
		pingMsg.setEntityId(Player.getSingleton().getId());
		pingMsg.setParams(String.valueOf(System.currentTimeMillis()));

		client.send(pingMsg);

		MessageBundle msg;
		for(msg = client.receive(); msg!=null; msg = client.receive()) {
			String playerCount = msg.getParams()[0];
			String incomingAddress = msg.getSocketAddress().getAddress()
					.getHostAddress();
			int incomingPort = msg.getSocketAddress().getPort();

			List<String> currentRow = new ArrayList<String>();

			currentRow.add(incomingAddress);
			currentRow.add(incomingPort + "");
			currentRow.add(playerCount);
			table.add(currentRow);
		}

		results.setStatus(Status.SUCCESS);
	}
}

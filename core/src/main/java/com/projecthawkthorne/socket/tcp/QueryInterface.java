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
	
	public QueryInterface(HawkthorneGame game){
		this.game = game;
		this.json = new Json();
		this.json.setOutputType(OutputType.json);
	}

	
	public void getServerList(final Results results, final List<List<String>> table) {
		MessageBundle pingMsg = new MessageBundle();
		MessageBundle msg = new MessageBundle();

		String ipBase = "192.168.0.";
		table.clear();
		for (int i = 0; i <= 255; i++) {
		    String ipAddress = ipBase+i;
			int port;
			try {
				port = Integer.valueOf(System.getenv("HAWK_PORT"));
			} catch (Exception e) {
				port = 12345;
			}
			List<String> currentRow = new ArrayList<String>();
			Client client;
			try {
				client = Client.getNewQueryClient(
						InetAddress.getByName(ipAddress), port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				continue;
			}

			pingMsg.setCommand(Command.GETPLAYERCOUNT);
			pingMsg.setEntityId(Player.getSingleton().getId());
			pingMsg.setParams(String.valueOf(System.currentTimeMillis()));
			client.send(pingMsg);

			msg = client.receive();
			if (msg != null) {
				String incomingAddress = msg.getSocketAddress().getHostName();
				String incomingPort = String.valueOf(msg.getSocketAddress()
						.getPort());
				String playerCount = msg.getParams()[0];

				currentRow.add(ipAddress);
				currentRow.add(port + "");
				currentRow.add(playerCount);
				table.add(currentRow);
			}

			results.setStatus(Status.SUCCESS);
		}
	}
}

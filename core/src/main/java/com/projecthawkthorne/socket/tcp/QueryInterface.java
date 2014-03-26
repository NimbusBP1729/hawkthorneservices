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

	
	public void getServerList(final Results results, final List<List<String>> table) throws UnknownHostException {
		final MessageBundle pingMsg = new MessageBundle();
		final byte[] ip = InetAddress.getLocalHost().getAddress();

		table.clear();
		for (int i = 0; i < 255; i++) {
		    ip[3] = (byte) i;
			int port;
			try {
				port = Integer.valueOf(System.getenv("HAWK_PORT"));
			} catch (Exception e) {
				port = 12345;
			}
			final Client client;
			try {
				client = Client.getNewQueryClient(InetAddress.getByAddress(ip), port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				continue;
			}

			pingMsg.setCommand(Command.GETPLAYERCOUNT);
			pingMsg.setEntityId(Player.getSingleton().getId());
			pingMsg.setParams(String.valueOf(System.currentTimeMillis()));
			
			
			new Thread(){
				public void run(){
					client.send(pingMsg);

					MessageBundle msg = client.receive();
					if (msg != null) {
						String playerCount = msg.getParams()[0];
						String incomingAddress = msg.getSocketAddress().getAddress().getHostAddress();
						int incomingPort = msg.getSocketAddress().getPort();
						
						List<String> currentRow = new ArrayList<String>();

						currentRow.add(incomingAddress);
						currentRow.add(incomingPort + "");
						currentRow.add(playerCount);
						table.add(currentRow);
					}
				}
			}.start();

			results.setStatus(Status.SUCCESS);
		}
	}
}

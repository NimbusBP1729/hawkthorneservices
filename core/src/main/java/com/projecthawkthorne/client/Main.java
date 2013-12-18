package com.projecthawkthorne.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.projecthawkthorne.socket.Client;

public class Main {
	public static void main(String[] args) {

//		// // run server within client
//		// new Thread() {
//		// public void run() {
//		// com.projecthawkthorne.server.Main.main(argv);
//		// }
//		// }.start();
//		// try {
//		// Thread.sleep(3000);
//		// } catch (InterruptedException e) {
//		// e.printStackTrace();
//		// }
//		CommandLineParser parser = new PosixParser();
//		Options options = new Options();
//		options.addOption("p", "port", true, "select a port");
//		options.addOption("a", "address", true, "select an IP address");
//		options.addOption("d", "debug", false, "draws debug bounding boxes");
//		CommandLine line = null;
//		try {
//			line = parser.parse(options, args);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		int port = 12346;
//		if (line.hasOption("port")) {
//			port = Integer.parseInt(line.getOptionValue("port"));
//		}
//		String address = "localhost";
//		if (line.hasOption("address")) {
//			address = line.getOptionValue("address");
//		}
//		// already on by default, but just in case
//		if (line.hasOption("debug")) {
//			HawkthorneGame.DEBUG = true;
//		}
//
//		try {
//			Client.serverIp = InetAddress.getByName(address);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		Client.serverPort = port;
//		Client.init();
//
//		new LwjglApplication(new HawkthorneGame(), "JttCoH", 912, 528, false);
	}
}

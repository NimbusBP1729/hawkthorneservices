package com.projecthawkthorne.socket;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class SocketUtils {
	
	private static final String[] EMPTY = new String[0];

	private static MessageBundle mb = new MessageBundle();

	private static String PARAM_SEPARATOR = ",";
	private static String MEMBER_SEPARATOR = "|";

	public static String bundleToString(MessageBundle mb) {
		String str = mb.getEntityId() + MEMBER_SEPARATOR + mb.getCommand();
		String[] params = mb.getParams();
		if (params != null) {
			str += MEMBER_SEPARATOR + StringUtils.join(params, PARAM_SEPARATOR);
		}
		return str;
	}

	public static MessageBundle packetToBundle(DatagramPacket packet) {
		if (packet == null) {
			return null;
		}
		String[] chunks = StringUtils.split(new String(packet.getData()),MEMBER_SEPARATOR);
		UUID id = UUID.fromString(chunks[0]);
		Command command = Command.valueOf(chunks[1]);
		String[] paramChunks;
		if(chunks.length>2){
			paramChunks = StringUtils.split(chunks[2], PARAM_SEPARATOR);
		}else{
			paramChunks = EMPTY;			
		}
		mb.setCommand(command);
		mb.setEntityId(id);
		mb.setParams(paramChunks);
		mb.setSocketAddress((InetSocketAddress) packet.getSocketAddress());
		return mb;

	}
	public static void clearPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		for (int i = 0; i < data.length; i++) {
			data[i] = '\0';
		}
	}

	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

}

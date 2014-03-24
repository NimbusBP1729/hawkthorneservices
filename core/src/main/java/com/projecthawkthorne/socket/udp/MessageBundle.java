package com.projecthawkthorne.socket.udp;

import java.net.InetSocketAddress;

import com.projecthawkthorne.content.UUID;

public class MessageBundle {
	private UUID entityId;
	private Command command;
	private String[] params;
	private InetSocketAddress socketAddress;

	public UUID getEntityId() {
		return entityId;
	}

	public void setEntityId(UUID entityId) {
		this.entityId = entityId;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String... params) {
		this.params = params;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public void setSocketAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

}

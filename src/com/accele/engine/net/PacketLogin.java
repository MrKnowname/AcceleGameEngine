package com.accele.engine.net;

public class PacketLogin extends Packet {

	private String username;
	
	public PacketLogin(byte[] data) {
		super(LOGIN);
		username = readData(data);
	}

	@Override
	public void writeData(Client client) {
		client.send(getData());
	}

	@Override
	public void writeData(Server server) {
		server.broadcast(getData());
	}
	
	@Override
	public byte[] getData() {
		return (1 + username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}

}

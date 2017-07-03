package com.accele.engine.net;

public abstract class Packet {

	public static final int INVALID = 0x00;
	public static final int LOGIN = 0x01;
	public static final int DISCONNECT = 0x02;
	
	protected int id;
	
	public Packet(int id) {
		this.id = id;
	}
	
	public abstract void writeData(Client client);
	public abstract void writeData(Server server);
	public abstract byte[] getData();

	public String readData(byte[] data) {
		return new String(data);
	}
	
	public int getId() {
		return id;
	}
	
}

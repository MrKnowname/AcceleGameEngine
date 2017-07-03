package com.accele.engine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.entity.MultiplayerEntity;

import test.Test;

public class Server extends Thread {

	private static final int PORT = 8088;
	
	private Engine engine;
	private DatagramSocket socket;
	private int numUsers;
	private List<MultiplayerEntity> entities;
	
	public Server(Engine engine) {
		this.engine = engine;
		try {
			this.socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.entities = new ArrayList<>();
	}
	
	@Override
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(data, packet.getAddress(), packet.getPort());
			//System.out.println("Client: " + new String(packet.getData()));
		}
	}
	
	public void parsePacket(byte[] data, InetAddress ip, int port) {
		String message = new String(data).trim();
		int protocol = Integer.parseInt(message.substring(0, 1));
		message = message.substring(1);
		switch (protocol) {
		case Packet.INVALID:
			break;
		case Packet.LOGIN:
			PacketLogin packet = new PacketLogin(data);
			System.out.println("[" + ip.getHostAddress() + ":" + port + "] > " + packet.getUsername() + " has connected");
			MultiplayerEntity e = null;
			synchronized (this) {
				e = new Test.PlayerMP(engine, new Vector3f(100, 10, 100), ip, port, packet.getUsername());
			}
			if (e != null) {
				engine.getRegistry().register(e);
				entities.add(e);
			}
			break;
		case Packet.DISCONNECT:
			break;
		}
	}
	
	public void send(byte[] data, InetAddress ip, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcast(byte[] data) {
		for (MultiplayerEntity e : entities)
			send(data, e.getIp(), e.getPort());
	}
	
	public void addEntity(MultiplayerEntity e) {
		entities.add(e);
	}
	
}

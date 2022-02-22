package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receiver extends Thread {
	private DataInputStream dis;

	public Receiver(Socket socket) {
		try {
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (dis != null) {
			try {
				System.out.println(dis.readUTF()); // Sender 쓰레드의 writeUTF를 사용할 때마다 while문이 돈다.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
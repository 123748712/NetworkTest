package kr.or.ddit.basic;

import java.io.IOException;
import java.net.Socket;

import kr.or.ddit.tcp.Receiver;
import kr.or.ddit.tcp.Sender;

public class TcpChatClient {
	public static void main(String[] args) {
		
		try {
			Socket socket = new Socket("192.168.145.5", 7777);
			
			System.out.println("서버에 연결되었습니다.");
			
			Sender sender = new Sender(socket);
			Receiver receiver = new Receiver(socket);
			
			sender.start();
			receiver.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
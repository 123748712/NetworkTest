package kr.or.ddit.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpClient {
	private DatagramSocket ds;
	private DatagramPacket dp;

	private byte[] msg; // 데이터가 저장될 공간으로 byte배열을 생성한다.

	public UdpClient() {
		try {
			msg = new byte[100];

			// 소켓객체 생성(포트번호 명시하지 않으면 임의의 포트번호 할당됨)
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	// 시작 메서드
	public void start() {
		try {
			InetAddress serverAddr = InetAddress.getByName("192.168.145.3");

			dp = new DatagramPacket(msg, 1, serverAddr, 8888); // 전송을 위한 정보 담기
			ds.send(dp); // 패킷 전송 (block상태 x, send 후 바로 아래 코드 실행)

			dp = new DatagramPacket(msg, msg.length);
			ds.receive(dp); // 패킷 수신 (send한 후 receive를 위한 block상태)

			System.out.println("현재 서버 시간 => " + new String(dp.getData())); // byte배열로 데이터 가져온 후 String객체로 변환
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new UdpClient().start();
	}
}

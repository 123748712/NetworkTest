package kr.or.ddit.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class udpServer {
	// UDP방식 소켓, 패킷
	private DatagramSocket ds;
	private DatagramPacket dp;
	
	private byte[] msg; // 패킷송수신을 위한 바이트 배열 선언
	
	public udpServer() {
		try {
			// 메시지 수신을 위한 포트번호 설정
			ds = new DatagramSocket(8888);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	// 시작 메서드
	public void start() throws IOException {
		while(true) {
			// 데이터를 수신하기 위한 패킷을 생성한다.
			msg = new byte[1]; // 메시지를 담기 위한 1byte 배열 생성
			// byte기반 데이터를 1byte씩 server로 보내기 위함 (byte => 모든 데이터의 기반)
			dp = new DatagramPacket(msg, msg.length); // 1byte배열을 담았기 때문에 1byte만 전송됨
			// 상대방이 보낸 데이터를 패킷으로 받기 위해 생성 (실제 데이터가 저장됨)
			
			System.out.println("패킷 수신 대기중...");
			
			// 패킷을 통해 데이터를 수신(receive)한다.
			ds.receive(dp); // receive를 하지 않으면 send를 하고 있어도 무용지물
			// block 상태이고, 상대방이 send 하는 즉시 해제 (=accept())
			
			System.out.println("패킷 수신 완료.");
			
			// 수신한 패킷으로부터 client의 IP주소와 Port를 얻는다.
			InetAddress iAddr = dp.getAddress(); // 상대방의 Address객체를 얻을 수 있음
			int port = dp.getPort(); // int 타입의 Port도 얻을 수 있음
			// 추출 이유는 서버 시간을 보내주기 위해서 상대방의 주소 및 Port를 얻는 작업
			// UDP는 비연결형이기 때문에 정보를 알아야 전송이 가능
			
			// 서버의 현재 시간을 시분초 형태[hh:mm:ss]로 반환한다.
			SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:dd]"); // 서버 현재 시간을 추출
			String time = sdf.format(new Date());
			msg = time.getBytes(); // 시간 문자열을 byte배열로 변환한다.
			
			// 패킷을 생성해서 client에게 전송(send)한다.
			dp = new DatagramPacket(msg, msg.length, iAddr, port);
			// 데이터를 담음 패킷을 생성한 후 (데이터, 보낼 데이터의 크기, 상대방의 IP, port)를 작성하면 전송할 준비 완료
			ds.send(dp); // 패킷 전송하기
		}
	}
	public static void main(String[] args) {
		try {
			new udpServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

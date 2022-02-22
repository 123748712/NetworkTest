package kr.or.ddit.udp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpFileReceiver {
	private DatagramSocket ds;
	private DatagramPacket dp;

	private byte[] buffer;

	public UdpFileReceiver(int port) {
		try {
			// 데이터 수신을 위한 포트번호 설정
			ds = new DatagramSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 데이터 수신하기
	// return =>
	//
	public byte[] receiveData() throws IOException {
		buffer = new byte[1000]; // 버퍼 초기화
		dp = new DatagramPacket(buffer, buffer.length);
		ds.receive(dp);

		return dp.getData();
	}

	// 시작 메서드
	public void start() throws IOException {
		long fileSize = 0;
		long totalReadBytes = 0;
		int readBytes = 0;

		System.out.println("파일 수신 대기중...");

		String str = new String(receiveData()).trim();

		if (str.equals("start")) {
			// Sender에서 전송을 시작한 경우
			// (Sender에서 첫 데이서 송신을 start 로 하기 때문)
			str = new String(receiveData()).trim();

			// 전송 파일명 수신하기
			FileOutputStream fos = new FileOutputStream("d:/D_Other/udpdown/" + str);

			// 파일 크기(bytes) 정보 수신
			str = new String(receiveData()).trim();
			fileSize = Long.parseLong(str);

			long startTime = System.currentTimeMillis();

			while (true) {
				byte[] data = receiveData();
				// 보낼 데이터 또는 받은 데이터의 길이 구하기
				readBytes = dp.getLength();

				fos.write(data, 0, readBytes);
				// 받은 데이터를 0부터 몇 byte까지 전송할지 알려줌

				totalReadBytes += readBytes;
				System.out.println("진행 상태 : " + totalReadBytes + "/" + fileSize + " Byte(s) ("
						+ totalReadBytes * 100 / fileSize + " %)");

				if (totalReadBytes >= fileSize) { // 데이터의 크기가 크거나 같다면 모두 전송받은 것
					break; // 전송을 위한 반목문을 빠져나온다.
				}
			}
			
			long endTime = System.currentTimeMillis();
			long diffTime = (endTime - startTime);
			double transferSpeed = fileSize / diffTime;

			System.out.println("걸린 시간 : " + diffTime + " (ms)");
			System.out.println("평균전송 속도 : " + transferSpeed + " Bytes/ms");
			System.out.println("수신 완료...");
			
			fos.close();
			ds.close();
		} else {
			System.out.println("비정상 데이터 발견");
			ds.close(); // 전송할 데이터가 아닌 데이터가 온다면 소켓을 닫는다.
			}
	}
	public static void main(String[] args) {
		try {
			new UdpFileReceiver(8888).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

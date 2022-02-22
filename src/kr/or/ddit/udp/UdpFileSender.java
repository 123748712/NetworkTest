package kr.or.ddit.udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.activation.FileDataSource;

public class UdpFileSender {
	private DatagramSocket ds;
	private DatagramPacket dp;

	private InetAddress receicerAddr;
	private int port;

	public UdpFileSender(String receiverIp, int port) {
		try {
			ds = new DatagramSocket();
			receicerAddr = InetAddress.getByName(receiverIp);
			this.port = port;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UdpFileSender(String receiverIp) {
		try {
			ds = new DatagramSocket();
			receicerAddr = InetAddress.getByName(receiverIp);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 시작 메서드
	public void start() throws InterruptedException {
		File file = new File("d:/D_Other/aa.gif");

		long fileSize = file.length();
		long totalReadBytes = 0;
		long startTime = System.currentTimeMillis();

		try {
			// 전송 시작을 알려주기 위한 문자열 전송
			sendData("start".getBytes());

			// 파일명을 전송
			sendData(file.getName().getBytes());

			// 총 파일 사이즈 정보를 알려줌
			sendData(String.valueOf(fileSize).getBytes());

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1000];
			while (true) {
				Thread.sleep(10); // 패킷전송 간의 간격을 주기 위함

				int readBytes = fis.read(buffer, 0, buffer.length);
				// read 때마다 1000씩 buffer에 저장되며,
				// 마지막 1000이 아닌 더 작은 수가 남았을땐 그 작은수가 리턴되어 전부 buffer에 저장됨

				if (readBytes == -1) { // 다 읽은 경우
					break;
				}
				sendData(buffer, readBytes); // 읽어온 파일 내용 전송
				// 읽어온 파일을 전부 저장한 buffer를 보낸다.

				totalReadBytes += readBytes;
				System.out.println("진행 상태 : " + totalReadBytes + "/" + fileSize + " Byte(s) ("
						+ totalReadBytes * 100 / fileSize + " %)");
			}
			long endTime = System.currentTimeMillis();
			long diffTime = (endTime - startTime);
			double transferSpeed = fileSize / diffTime;

			System.out.println("걸린 시간 : " + diffTime + " (ms)");
			System.out.println("평균전송 속도 : " + transferSpeed + " Bytes/ms");
			System.out.println("전송 완료...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// data => 전송할 바이트 배열
	public void sendData(byte[] data) throws IOException {
		sendData(data, data.length);

	}

	// 바이트배열 데이터 전송하기
	// data => 전송할 바이트 배열
	// length => 전송할 바이트 크기
	public void sendData(byte[] data, int length) throws IOException {
		dp = new DatagramPacket(data, length, receicerAddr, port);
		ds.send(dp);
	}
	public static void main(String[] args) {
		try {
			new UdpFileSender("192.168.145.5", 8888).start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

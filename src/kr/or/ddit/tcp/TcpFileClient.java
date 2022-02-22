package kr.or.ddit.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpFileClient {
	// 클라이언트는 서버에 접속하여 서버가 보내주는 파일을 D:/C_lib 폴더에 저장한다.
	private Socket socket;
	private FileOutputStream fos;
	
	// 시작 메서드
	public void clientStart() {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			socket = new Socket("192.168.145.3", 7777);
			System.out.println("파일 다운로드 시작...");
			
			fos = new FileOutputStream("d:/C_Lib/tulip.jpg");
			
			// 보조스트림, 기반스트림은 File
			bis = new BufferedInputStream(socket.getInputStream());
			bos = new BufferedOutputStream(fos);
			
			int data = 0;
			while((data = bis.read()) != -1) {
				bos.write(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("파일 다운로드 완료 !");
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static void main(String[] args) {
		new TcpFileClient().clientStart();
	}
}
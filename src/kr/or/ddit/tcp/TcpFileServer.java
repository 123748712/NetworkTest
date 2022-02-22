package kr.or.ddit.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpFileServer {
	// 서버는 클라이언트가 접속하면 서버 컴퓨터의 D_Other 폴더에 있는 Tulips.jpg 파일을 클라이언트로 전송한다.
	
	private ServerSocket server;
	private Socket socket;
	private FileInputStream fis;
	private File file = new File("d:/D_Other/Tulips.jpg");
	
	public void serverStart() { // 클라이언트한테 바로 이미지를 전송하겠다.
		try {
			server = new ServerSocket(7777);
			System.out.println("서버 준비 완료.");
			
			while(true) {
				System.out.println("파일 전송 대기중...");
				socket = server.accept(); // 클라이언트와 소켓이 연결된 후 파일 전송 시작
				System.out.println("파일 전송 시작...");
				fis = new FileInputStream(file);
				
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
				int data = 0;
				while((data = bis.read()) != -1) {
					bos.write(data);
				}
				// output으로 상대방에게 출력한 후 다시 while문에 있는 accept() 메서드로 돌아감.
				bis.close();
				bos.close();
				
				System.out.println("파일 전송 완료...");
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new TcpFileServer().serverStart();
	}
}
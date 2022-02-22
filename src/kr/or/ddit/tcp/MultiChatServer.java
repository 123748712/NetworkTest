package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.or.ddit.tcp.MultiChatServer.ServerReceiver;

public class MultiChatServer {
	// 대화명, 클라이언트의 Socket을 저장하기 위한 Map변수 선언
	Map<String, Socket> clients;

	// 생성자
	public MultiChatServer() {
		// 동기화 처리가 가능하도록 Map객체 생성
		clients = Collections.synchronizedMap(new HashMap<>());
	}

	// 서버 시작
	public void serverStart() {
		Socket socket = null;
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("서버가 시작되었습니다.");

			while (true) { // 메인스레드가 하는 역할 (접속하려는 Socket들의 접속을 대기, 연결시켜줌)
				// 클라이언트의 접속을 대기한다.
				socket = serverSocket.accept();

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "] 에서 접속하였습니다.");

				// 메시지 전송을 처리하는 스레드 생성 및 실행
				// 상대방이 보낸 메세지를 서버가 받은 후 모든 소켓에 output 해주기만 하면 되기 때문
				ServerReceiver receiver = new ServerReceiver(socket);

				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void whisperMessage(String msg) {
		String[] whisper = msg.split(" ");
		Iterator<String> it = clients.keySet().iterator();
			try {
			if (whisper[1].equals(it.next())) {

					DataOutputStream dos = new DataOutputStream(clients.get(whisper[1]).getOutputStream());

					dos.writeUTF("귓속말 >> " + whisper[2]);
			}
				} catch (IOException e) {
					e.printStackTrace();
			}
		}

	// 대화봥 즉, Map에 저장된 전체 유저에게 안내메시지를 전송하는 메서드
	public void sendMessage(String msg) {
		// Map에 저장된 유저의 대화명 리스트를 추출(key값 구하기)
		Iterator<String> it = clients.keySet().iterator();
		try {
			while (it.hasNext()) {
				String name = it.next(); // 대화명(key) 구하기
				// 대화명에 해당하는 Socket 객체에서 OutputStream 가져오기
				DataOutputStream dos = new DataOutputStream(clients.get(name).getOutputStream());
				// get으로 꺼낸 Socket들에 메시지를 전송하기 위함.
				dos.writeUTF(msg); // 메시지 보내기
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
}

	public void sendMessage(String msg, String from) {
		if (msg.startsWith("/w")) {
			whisperMessage(msg);
		} else {
			sendMessage("[" + from + "]" + msg); // 코드가 동일하기 때문에 위에 만든 sendMessage를 호출한다.
			// 파라미터가 두개인 sendMessage를 만든다.
			// 메시지를 누가 보냈는지 조금 더 자세하게 알기 위해.
		}
	}

	// 서버에서 클라이언트로 메세지를 전송할 때 Thread를 Inner클래스로 정의
	// Inner클래스에서는 부모클래스의 멤버들을 직접 사용할 수 있다.
	class ServerReceiver extends Thread {
		private Socket socket;
		private DataInputStream dis;
		private String name;

		public ServerReceiver(Socket socket) {
			this.socket = socket;

			try {
				dis = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				// 서버에서는 클라이언트가 보내는 최초의 메시지 즉, 대화명을 수신해야 한다.
				name = dis.readUTF();

				// 대화명을 받아서 다른 모든 클라이어트에게 대화방 참여 메시지를 보낸다.
				sendMessage("#" + name + "님이 입장했습니다.");

				// 대화명과 소켓정보를 Map에 저장한다.
				clients.put(name, socket);
				System.out.println("현재 서버 접속자 수는 " + clients.size() + "명 입니다.");

				// 이 후의 메시지 처리는 반복문으로 처리한다.
				// 한 클라이언트가 보낸 매세지를 다른 모든 클라이언트에게 보내준다.
				while (dis != null) { // null이 아님 즉, 메시지가 있다면 모든 소켓에 읽은 데이터를 보낸다.
					sendMessage(dis.readUTF(), name); //
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 이 finally영역이 실행된다는 것은 클라리언트의 접속이 종료되었다는 의미이다.
				sendMessage("#" + name + "님이 나가셨습니다.");

				// Map에서 해당 대화명을 삭제한다.
				clients.remove(name);

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "] 에서 접속을 종료했습니다.");
				System.out.println("현재 접속자 수는 " + clients.size() + "명 입니다.");
			}
		}
	}

	public static void main(String[] args) {
		new MultiChatServer().serverStart();
	}
}

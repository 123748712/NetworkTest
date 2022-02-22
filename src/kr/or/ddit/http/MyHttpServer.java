package kr.or.ddit.http;

import java.awt.HeadlessException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.StringTokenizer;

// 간단한 웹서버 예제
public class MyHttpServer {
	private final int port = 80;
	private final String encoding = "UTF-8";

	// 서버 시작
	public void start() {
		try (ServerSocket serverSocket = new ServerSocket(this.port)) { // Try-with-resources
			// () 안의 자원들이 자동으로 반납됨
			while (true) {
				try {
					Socket socket = serverSocket.accept();

					HttpHandler handler = new HttpHandler(socket);

					new Thread(handler).start(); // 요청 처리 시작

				} catch (IOException e) {
					System.out.println("커넥션 오류");
					e.printStackTrace();
				} catch (RuntimeException e) {
					System.out.println("알수없는 오류");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("서버 시작 오류");
			e.printStackTrace();
		}
	}

	// Http 요청 처리를 위한 Runnable 클래스
	private class HttpHandler implements Runnable {
		private final Socket socket;

		public HttpHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			OutputStream out = null;
			BufferedReader br = null;

			try {
				out = new BufferedOutputStream(socket.getOutputStream());

				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// 요청 헤더정보 파싱하기
				StringBuilder request = new StringBuilder();
				while (true) {
					String str = br.readLine(); // 소켓을 통해 한줄씩 읽음
					// 첫줄엔 Request Line이 담김
					// 두번째부턴 header정보가 담김

					// Empty Line 체크
					if (str.equals(""))
						break;

					request.append(str + "\n"); // 한줄씩 read한 줄들을 저장함
					// Request Line + header 정보까지만 담겨있음
				}
				System.out.println("요청헤더 :\n" + request);
				System.out.println("================================");

				String reqPath = "";

				// 요청 페이지 정보 가져오기
				StringTokenizer st = new StringTokenizer(request.toString()); // token화 시킬 데이터, 구분자(default는 공백)
				// String객체를 token화 시키는 클래스
				// token => 구분자를 통해 작은 조각으로 나눠진 데이터

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.startsWith("/")) { // ex) GET /aaa/abc.jpg HTTP/1.1 | 공백을 기준으로 "/"로 시작하는 token을 넣어주려함.
						reqPath = token; // /aaa/abc.jpg가 담긴다.
					}
				}

				// URL 디코딩 처리 (한글깨짐 문제)
				reqPath = URLDecoder.decode(reqPath, encoding);

				String filePath = "./WebContent" + reqPath; // 웹 컨텐츠 경로 설정

				// 해당 파일 이름을 이용하여 Content-type정보 추출하기
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
				//

				// CSS파일인 경우 인식이 안돼서 추가함
				if (contentType == null && filePath.endsWith(".css")) {
					contentType = "text/css";
				}
				System.out.println("Content-Type : " + contentType);

				File file = new File(filePath);
				if (file.exists()) {
					makeErrorPage(out, 404, "Not Found");
					return;
				}
				byte[] body = makeResponseBody(filePath);

				byte[] header = makeResponseHeader(body.length, contentType);

				// 요청헤더가 HTTP/1.0이나 그 이후의 버전을 지원할 경우 MIME헤더를 전송한다.
				if (request.toString().indexOf("HTTP/") != -1) {
					out.write(header); // 응답 헤더 보내기
				}
				System.out.println("응답 헤더 :\n" + new String(header));
				System.out.println("===============================");

				out.write(body); // 응답 내용(body) 보내기
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close(); // 소켓 닫기 (연결 끊기)
				} catch (IOException e) {
					e.printStackTrace();
				} // 소켓 닫기(연결 끊기)
			}
		}
	}

	// 에러 페이지 생성
	private void makeErrorPage(OutputStream out, int errCode, String errMsg) {
		String statusLine = "HTTP/1.1" + " " + errCode + " " + errMsg;
		
		try {
			out.write(statusLine.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 응답헤더 생성하기
	private byte[] makeResponseHeader(int length, String contentType) {
		String header = "HTTP/1.1 200 OK\r\n"
				+ "Server: MyHttpServer 1.0\r\n"
				+ "Content-length: " + length + "\r\n"
				+ "Content-type: " + contentType
				+ "; charset=" + this.encoding
				+ "\r\n\r\n";// \r\n => enter와 동일 (Empty Line)
		
		return header.getBytes();
	}

	// 응답내용 생성하기
	private byte[] makeResponseBody(String filePath) {
		FileInputStream fis = null;
		byte[] data = null;
		
		try {
			File file = new File(filePath);
			data = new byte[(int) file.length()];
			
			fis = new FileInputStream(file);
			fis.read(data);
			} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return data;
	}
}

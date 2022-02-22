package kr.or.ddit.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class URLConnectionTest {
	public static void main(String[] args) throws IOException {

		// URLConnection => 애플리케이션과 URL간의 통신 연결을 위한 추상 클래스

		// 특정 서버(네이버)의 정보와 파일 내용을 출력하는 예제
		URL url = new URL("https://www.naver.com/index.html");

		// URLConnection 객체 구하기
		URLConnection urlConn = url.openConnection();

		System.out.println("Content-Type : " + urlConn.getContentType());
		System.out.println("Encoding : " + urlConn.getContentEncoding());
		System.out.println("Content : " + urlConn.getContent());
		System.out.println();

		// 전체 Header정보 출력하기
		Map<String, List<String>> headerMap = urlConn.getHeaderFields();

		Iterator<String> it = headerMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println(key + " : " + headerMap.get(key));
		}
		System.out.println("====================================");

		// 해당 호스트의 페이지 내용 가져오기

		// 파일을 읽어오기 위한 스트림 생성
		// 방법 1 => URLConnection의 getInputStream() 이용
		// InputStream is = urlConn.getInputStream();

		// 방법 2 => URL 객체의 openStream()메서드 이용하기
//		InputStream is = url.openStream();
		InputStream is = (InputStream) urlConn.getContent();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		// 내용 출력하기
		String str = "";
		while ((str = br.readLine()) != null) {
			System.out.println(str);
		}
		br.close();
	}
}
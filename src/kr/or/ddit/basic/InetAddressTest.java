package kr.or.ddit.basic;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {
	public static void main(String[] args) throws UnknownHostException {
		// InetAddress => IP주소를 다루기 위한 클래스

		// getByName => www.naver.com 또는 SEM-PC같은 머신이름이나 IP주소를 파라미터를 이용하여 유효한 InetAddress 객체를 제공한다.
		// (IP주소 자체를 넣으면 주소 구성 자체의 유효성 정도만 체크가 이루어짐.

		// 네이버 사이트의 ip정보 가져오기
		InetAddress naverIp = InetAddress.getByName("www.naver.com");
		System.out.println("Host Name => " + naverIp.getHostName());
		System.out.println("Host Address => " + naverIp.getHostAddress());
		System.out.println();
		
		// 자기 자신 컴퓨터의 IP정보 가져오기
		InetAddress localIp = InetAddress.getLocalHost();
		System.out.println("내 컴퓨터의 Host Name => " + localIp.getHostName());
		System.out.println("내 컴퓨터의 Host Address => " + localIp.getHostAddress());
		System.out.println();
		
		// IP주소가 여러개인 호스트의 정보 가져오기
		InetAddress[] naverIps = InetAddress.getAllByName("naver.com");
		for(InetAddress addr : naverIps) {
			System.out.println(addr);
		}
	}
}
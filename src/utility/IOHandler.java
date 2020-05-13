package utility;

import java.util.Date;
import java.util.Scanner;

public class IOHandler {
	
	// 싱글톤 패턴
	private static IOHandler _instance;
	
	// IOHandler 사용 시 IOHandler.getInstance().메소드명 으로 사용하면 됨.
	public static IOHandler getInstance()
	{
		if(_instance == null)
			_instance = new IOHandler();
		return _instance;
	}
	
	// 콘솔 or 텍스트 파일에 로깅
	public void log(String s) {
		// [날짜+시간+사용자명]+오류명 이런식으로 로깅하게 할 예정임
		System.out.println("[" + new Date() + "]" + s);
	}
	
	// 예외 발생 시 사용할 수 있는 로그 메소드
	public void log(String from, Exception e) {
		System.out.println("[" + new Date() + "][" + from + "]\n" + e.getMessage());
//		e.printStackTrace();
	}
	
	public int getIntByUser() {
		try {
			Scanner scn = new Scanner(System.in);
			System.out.print(">> ");
			return scn.nextInt();			
		}
		catch(Exception e) {
			log("IOHandler.getIntByUser", e);
			return -987654321;
		}
	}
	
	public String getLineByUser(String msg) {
		try {
			if(msg != null) {
				System.out.println(msg);
			}
			Scanner scn = new Scanner(System.in);
			System.out.print(">> ");
			return scn.nextLine();			
		}
		catch(Exception e) {
			log("IOHandler.getLineByUser",e);
			return null;
		}
	}
}

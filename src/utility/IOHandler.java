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
		System.out.println("[" + new Date() + ".Server]" + s);
	}
	
	public int getIntByUser() {
		try {
			Scanner scn = new Scanner(System.in);
			System.out.print(">> ");
			int userInput = scn.nextInt();
			return userInput;			
		}
		catch(Exception e) {
			log(e.getMessage());
			return -987654321;
		}
	}
}

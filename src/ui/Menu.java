package ui;

public class Menu {
	public void welcome() {
		System.out.println("==================================================");
		System.out.println("______ ______  _____  _   _   ___   _    _   ___  \r\n" + 
				"|  _  \\|  _  \\|  _  || \\ | | / _ \\ | |  | | / _ \\ \r\n" + 
				"| | | || | | || | | ||  \\| |/ /_\\ \\| |  | |/ /_\\ \\\r\n" + 
				"| | | || | | || | | || . ` ||  _  || |/\\| ||  _  |\r\n" + 
				"| |/ / | |/ / \\ \\_/ /| |\\  || | | |\\  /\\  /| | | |\r\n" + 
				"|___/  |___/   \\___/ \\_| \\_/\\_| |_/ \\/  \\/ \\_| |_/\r\n" + 
				"                                                  ");
		System.out.println("\t또나와 v0.1 서버 프로그램입니다.");
		System.out.println("\t또나와는 온라인 수집 가격 정보 공공데이터를 기반으로");
		System.out.println("\t온라인 수집 가격 정보를 누적하며");
		System.out.println("\t실시간 가격 알림 서비스를 제공하는 프로그램입니다.");
		System.out.println("==================================================");
	}
	
	public void show() {
		System.out.println("--------------------------------------------------");
		System.out.println("1. 웹 파싱 테스트");
		System.out.println("2. CSV 파일 덤프");
		System.out.println("3. 상품 조회 테스트");
		System.out.println("4. 종료");
		System.out.println("--------------------------------------------------");
	}
	

}

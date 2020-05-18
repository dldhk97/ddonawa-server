package application;

import console.ConsoleTask;
import console.Menu;
import db.DBConnector;
import parser.ParserManager;
import parser.SeleniumManager;
import utility.IOHandler;

public class Main{

	public static void main(String[] args) {
		run();
	}
	
	private static void run() {
		try {
			prepare();
			
			
			Menu menu = new Menu();
			menu.welcome();
			ConsoleTask ct = new ConsoleTask();
			
			while(true) {
				try {
					menu.show();
					int selected = IOHandler.getInstance().getIntByUser("메뉴를 입력하세요.");
					switch(selected) {
					case 1:
						ct.manualCollectInfo();
						break;
					case 2:
						ct.checkCollectedInfo();
						break;
					case 3:
						ct.manualDumpCSV();
						break;
					case 4:
						ct.manualAddAccount();
						break;
					case 5:
						ct.manualCheckAccount();
						break;
					case 6:
						ct.manualCheckCategory();
						break;
					case 7:
						ct.manualAddFavorites();
						break;
					case 8:
						ct.manualCheckFavorites();
						break;
					case 9:
						ct.workerThreadTest();
						break;
					case 0:
						shutdown();
						return;
					default:
						break;
					}
				}
				catch(Exception e) {
					IOHandler.getInstance().log("Main.run.loop", e);
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Main.run", e);
		}
	}
	
	private static void prepare() {
		IOHandler.getInstance().log("[SYSTEM]서버 실행 준비 중!");
		
		// 셀레니움 파서 미리 로드
		ParserManager.getInstance();
		
		IOHandler.getInstance().log("[SYSTEM]서버 실행 준비 완료!");
	}
	
	private static void shutdown() {
		DBConnector.getInstance().close();
		ParserManager.getInstance().close();
		IOHandler.getInstance().log("[SYSTEM]시스템 종료됨");
	}
	
}

package application;

import java.util.ArrayList;

import console.ConsoleTask;
import console.Menu;
import db.DBConnector;
import model.Product;
import parser.SeleniumManager;
import task.CSVReader;
import task.CollectedInfoTask;
import task.ProductTask;
import utility.IOHandler;

public class Main{

	public static void main(String[] args) {
		run();
	}
	
	private static void run() {
		try {
			// 프로그램이 종료될 때 호출되는 메소드
			Runtime rt = Runtime.getRuntime();
			rt.addShutdownHook(new Thread() {
				public void run() {
					SeleniumManager.getInstance().quit();
					DBConnector.getInstance().close();
					IOHandler.getInstance().log("[SYSTEM]시스템 종료됨");
				}
			});
			
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
	
}

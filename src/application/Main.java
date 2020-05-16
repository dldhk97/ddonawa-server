package application;

import java.util.ArrayList;

import db.DBConnector;
import model.Product;
import parser.SeleniumManager;
import task.CSVReader;
import task.CollectedInfoTask;
import task.ProductTask;
import ui.Menu;
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
			String userInput = null;
			
			while(true) {
				menu.show();
				int selected = IOHandler.getInstance().getIntByUser("메뉴를 입력하세요.");
				switch(selected) {
				case 1:
					CollectedInfoTask cit = new CollectedInfoTask();
					ProductTask pt = new ProductTask();
					
					userInput = IOHandler.getInstance().getLineByUser("DB에서 상품정보를 찾습니다. 검색어를 입력하세요.");
					
					ArrayList<Product> productList = pt.search(userInput);
					if(productList == null) {
						IOHandler.getInstance().log("검색 결과가 없습니다.");
						break;
					}
					int cnt = 0;
					for(Product p : productList) {
						System.out.println(cnt + ". " + p.getName());
						cnt++;
					}
					selected = IOHandler.getInstance().getIntByUser("수집정보를 갱신할 상품의 인덱스를 입력하세요.");
					cit.collect(productList.get(selected));
					
					break;
				case 2:
					CSVReader cr = new CSVReader();
					userInput = IOHandler.getInstance().getLineByUser("경로를 입력하세요.");
					cr.dumpCSV(userInput);
					break;
				case 3:
					ProductTask pt2 = new ProductTask();
					userInput = IOHandler.getInstance().getLineByUser("검색어를 입력하세요.");
					pt2.search(userInput);
					break;
				case 4:
					return;
				default:
					break;
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[Main.run]", e);
		}
	}
	
}

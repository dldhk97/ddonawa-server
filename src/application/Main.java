package application;

import parser.DanawaParser;
import parser.NaverShopParser;
import ui.Menu;
import utility.CSVReader;
import utility.IOHandler;

public class Main {

	public static void main(String[] args) {
		run();
		System.out.println("종료됨");
	}
	
	private static void run() {
		try {
			Menu menu = new Menu();
			menu.welcome();
			String userInput = null;
			
			while(true) {
				menu.show();
				int selected = IOHandler.getInstance().getIntByUser();
				switch(selected) {
				case 1:
					DanawaParser dp = new DanawaParser();
					userInput = IOHandler.getInstance().getLineByUser("검색어를 입력하세요.");
					dp.parse(userInput);
					break;
				case 2:
					NaverShopParser nsp = new NaverShopParser();
					userInput = IOHandler.getInstance().getLineByUser("검색어를 입력하세요.");
					nsp.parse(userInput);
					break;
				case 3:
					CSVReader cr = new CSVReader();
					userInput = IOHandler.getInstance().getLineByUser("경로를 입력하세요.");
					cr.read(userInput);
					break;
				case 4:
					return;
				default:
					break;
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}
	}
}

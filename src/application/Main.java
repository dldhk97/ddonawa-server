package application;

import parser.DanawaParser;
import parser.NaverShopParser;
import ui.Menu;
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
			String searchStr = null;
			
			while(true) {
				menu.show();
				int selected = IOHandler.getInstance().getIntByUser();
				switch(selected) {
				case 1:
					DanawaParser dp = new DanawaParser();
					searchStr = IOHandler.getInstance().getLineByUser("검색어 : ");
					dp.parse(searchStr);
					break;
				case 2:
					NaverShopParser nsp = new NaverShopParser();
					searchStr = IOHandler.getInstance().getLineByUser("검색어 : ");
					nsp.parse(searchStr);
					break;
				case 3:
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

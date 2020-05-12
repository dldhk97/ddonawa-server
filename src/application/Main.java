package application;

import parser.DanawaParser;
import parser.NaverShopParser;
import ui.Menu;
import utility.IOHandler;

public class Main {

	public static void main(String[] args) {
		run();
		System.out.println("Á¾·áµÊ");
	}
	
	private static void run() {
		try {
			Menu menu = new Menu();
			menu.welcome();
			
			while(true) {
				menu.show();
				int selected = IOHandler.getInstance().getIntByUser();
				switch(selected) {
				case 1:
					DanawaParser dp = new DanawaParser();
					dp.parse();
					break;
				case 2:
					NaverShopParser nsp = new NaverShopParser();
					nsp.parse();
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

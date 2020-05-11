package application;

import java.util.Scanner;

import ui.Menu;

public class Main {

	public static void main(String[] args) {
		run();
	}
	
	private static void run() {
		Menu menu = new Menu();
		menu.welcome();
		
		while(true) {
			menu.show();
			Scanner scn = new Scanner(System.in);
			String userInput = scn.nextLine();
		}
		
		
	}

}

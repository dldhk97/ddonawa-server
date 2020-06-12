package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import console.ConsoleTask;
import console.Menu;
import db.DBConnector;
import db.DBManager;
import network.NetworkInfo;
import parser.ParserManager;
import task.ServerTask;
import utility.IOHandler;

public class Main{
	
	private static ServerThread serverThread = null;

	public static void main(String[] args) {
		run();
	}
	
	private static void run() {
		try {
			// 셀레니움 준비
			prepareSelenium();
			
			// 서버 가동
			prepareServer();
			
			// CUI 설정
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
					case 0:
						shutdown();
						return;
					default:
						break;
					}
				}
				catch(Exception e) {
					IOHandler.getInstance().log("Main.run.loop");
					e.printStackTrace();
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Main.run", e);
		}
	}
	
	
	private static void prepareSelenium() {
		IOHandler.getInstance().log("[SYSTEM]서버 실행 준비 중!");
		
		// 셀레니움 파서 미리 로드
		ParserManager.getInstance();
		
		IOHandler.getInstance().log("[SYSTEM]서버 실행 준비 완료!");
	}
	
	private static void shutdown() {
		try {
			ParserManager.getInstance().close();
			if(serverThread != null)
				serverThread.close();
			DBConnector.getInstance().close();
			IOHandler.getInstance().log("[SYSTEM]시스템 종료됨");
		}
		catch (Exception e) {
			e.printStackTrace();
			IOHandler.getInstance().log("[SYSTEM]시스템 비정상적으로 종료됨");
		}
		
		
	}	
	
	private static void prepareServer() {
		try {
			serverThread = new ServerThread();
			Thread t = new Thread(serverThread);
			t.start();
			
			// 커넥션 풀 생성
			DBConnector.getInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
			IOHandler.getInstance().log("[SYSTEM]서버 스레드 생성 실패");
		}
		
	}
}

// 백그라운드에서 계속 돌아가면서 클라이언트의 요청을 받고, 담당 스레드 생성하여 할당함.
class ServerThread implements Runnable{
	
	private ServerSocket serverSocket;
	private ExecutorService threadPool;
	
	private boolean isRunning = false;
	
	public ServerThread() {
		try {
			int port = Integer.parseInt(NetworkInfo.SERVER_PORT.toString());
			serverSocket = new ServerSocket(port);
			threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run() {
		isRunning = true;
		Socket clientSocket = null;
		
		try {
			while(isRunning) {
				clientSocket = serverSocket.accept();		
				ServerTask st = new ServerTask(clientSocket);
				threadPool.execute(st);
			}
		}
		catch (SocketException se) {
			if(!isRunning) {
				IOHandler.getInstance().log("[SYSTEM]아마도 서버 소켓 종료에 의한 클라이언트 소켓 에러일듯. 괜찮음");				
			}else {
				IOHandler.getInstance().log("[SYSTEM]소켓 에러 발생");
				se.printStackTrace();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void close() {
		try {
			IOHandler.getInstance().log("[SYSTEM] 서버스레드 종료 요청");
			isRunning = false;
			serverSocket.close();
			IOHandler.getInstance().log("[SYSTEM] 서버 소켓 종료됨");
			
			threadPool.shutdown();
			IOHandler.getInstance().log("[SYSTEM] 스레드풀 종료됨");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

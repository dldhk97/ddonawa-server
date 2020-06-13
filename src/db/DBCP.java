package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import utility.IOHandler;

public class DBCP {
	private static final int DEFAULT_CONNECTION = 10;
	private static final int MAX_CONNECTION = 20;
	private static final int CONNECTION_VALID_TIMEOUT = 1;
	
	private static ArrayList<MyConnection> connections;
	
	// 싱글톤임
	private static DBCP _instance;
	
	public static DBCP getInstance() {
		if(_instance == null)
			_instance = new DBCP();
		return _instance;
	}
	
	private DBCP() {
		connections = new ArrayList<>(DEFAULT_CONNECTION);
		try {
			Class.forName(DBInfo.JDBC_DRIVER.toString());
			initConnections();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initConnections() {
		for(int i = 0 ; i < DEFAULT_CONNECTION ; i++) {
			Connection c;
			try {
				c = DriverManager.getConnection(DBInfo.DB_URL.toString(), DBInfo.USER_NAME.toString(), DBInfo.PASSWORD.toString());
				connections.add(new MyConnection(c));
				IOHandler.getInstance().log("[DBCP] initConnections : 커넥션 생성됨");
			} catch (SQLException e) {
				IOHandler.getInstance().log("[DBCP] initConnections : 커넥션 생성 실패");
				e.printStackTrace();
				
			}
		}
	}
	
	// 꺼내는 순간 busy로 바꿔서 꺼냄
	public MyConnection getMyConnection() throws Exception{
		MyConnection mc = getOrCreateConnection();
		if(mc == null) {
			throw new Exception("Failed to getOrCreateConnection");
		}
		mc.setBusy(true);
		return mc;
	}
	
	private MyConnection getOrCreateConnection(){
		for(MyConnection mc : connections) {
			try {
				if(mc != null && mc.getConnection().isValid(CONNECTION_VALID_TIMEOUT) && !mc.isBusy()) {
//					IOHandler.getInstance().log("[DBCP] getOrCreateConnection : 기존 커넥션 반환됨");
					return mc;
				}
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			if(connections.size() < MAX_CONNECTION) {
				IOHandler.getInstance().log("[DBCP] getOrCreateConnection : 신규 커넥션 생성시도");
				Connection c = DriverManager.getConnection(DBInfo.DB_URL.toString(), DBInfo.USER_NAME.toString(), DBInfo.PASSWORD.toString());
				MyConnection mc = new MyConnection(c);
				connections.add(new MyConnection(c));
				IOHandler.getInstance().log("[DBCP] getOrCreateConnection : 신규 커넥션 생성됨");
				return mc;
			}
			
		} catch (SQLException e) {
			IOHandler.getInstance().log("[DBCP] getOrCreateConnection : 신규 커넥션 생성 실패");
			e.printStackTrace();
		}
		
		return getOrCreateConnection();
	}
	
	public boolean closeAllConnection() {
		int closedCnt = 0;
		for(MyConnection mc : connections) {
			try {
				Connection c = mc.getConnection();
				boolean isClosed = true;
				if(c != null) {
					c.close();
					isClosed = c.isClosed();
					IOHandler.getInstance().log("[DBCP] closeAllConnection : 하나의 커넥션 종료 시도함.");
				}
					
				closedCnt = isClosed ? closedCnt + 1 : closedCnt;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		IOHandler.getInstance().log("[DBCP] closeAllConnection : 커넥션 모두 종료 시도 결과 : " + closedCnt + " VS " + connections.size());
		return closedCnt >= connections.size() ? true : false;
	}
	
	public boolean isConnected() {
		for(MyConnection mc : connections) {
			Connection c = mc.getConnection();
			try {
				if(c != null && !c.isClosed()) {
					return true;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}

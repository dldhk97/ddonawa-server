package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import utility.IOHandler;

public class DBConnector {
	
	// DB 접속용 변수
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"; //드라이버
	private final String DB_URL = "jdbc:mysql://localhost/ddonawa?characterEncoding=UTF-8&serverTimezone=UTC";	//접속할 서버
	 
	private final String USER_NAME = "ddonawa"; //DB에 접속할 사용자 이름을 상수로 정의
	private final String PASSWORD = "!1q2w3e4r"; //사용자의 비밀번호를 상수로 정의
	
	// 싱글톤 패턴
	private static DBConnector _instance;
	
	private Connection connection = null;
	private Statement state = null;
	
	// IOHandler 사용 시 IOHandler.getInstance().메소드명 으로 사용하면 됨.
	public static DBConnector getInstance()
	{
		if(_instance == null)
			_instance = new DBConnector();
		return _instance;
	}
	
	public DBConnector() {
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			state = connection.createStatement();
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[DBConnector.onCreate]" + e.getMessage());
		}
	}
	
	public boolean Select() throws Exception{
		String sql = "SELECT * FROM ddonawa.계정";
		ResultSet resultSet = state.executeQuery(sql);
		while(resultSet.next()) {
			String id = resultSet.getString("id");
			String password = resultSet.getString("pw");
			System.out.println("id:" + id + ", pw:" + password);
		}
		
		resultSet.close();
		return false;
	}
	
	public boolean Insert() throws Exception{
		String sql = "INSERT INTO `ddonawa`.`계정` (`id`, `pw`) VALUES ('user777', '8877');";
		int cnt = state.executeUpdate(sql);
		if(cnt > 0) {
//			IOHandler.getInstance().log("`" + sql + "` INSERT 성공");
			return true;
		}
		return false;
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			if(state != null) {
				state.close();
			}			
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[DBConnector.finalize]" + e.getMessage());
		}
		
		try {
			if(connection != null) {
				connection.close();
			}			
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[DBConnector.finalize]" + e.getMessage());
		}
		IOHandler.getInstance().log("MYSQL Closed");
	}
}

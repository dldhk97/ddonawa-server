package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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
			IOHandler.getInstance().log("DBConnector.onCreate", e);
		}
	}
	
	// sql문과 열 이름을 전달받으면 쿼리 후 해당되는 테이블을 받아옴
	public ArrayList<ArrayList<String>> Select(String sql, ArrayList<String> columnNames) throws Exception{
		ResultSet resultSet = state.executeQuery(sql);
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		while(resultSet.next()) {
			ArrayList<String> row = new ArrayList<String>();
			for(String cn : columnNames) {
				row.add(resultSet.getString(cn));
			}
			result.add(row);
		}
		
		resultSet.close();
		return result;
	}
	
	public int Insert(String sql) throws Exception{
		return state.executeUpdate(sql);
	}
	
	public int Insert(String dbName, String tableName, ArrayList<String> columnNames, ArrayList<String> values) throws Exception{
		
		StringBuilder sb = new StringBuilder("INSERT INTO `" + dbName + "`.`" + tableName + "` (");
		for(String columnName : columnNames) {
			sb.append("`" + columnName + "`, ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(") VALUES (");
		
		for(String value : values) {
			sb.append("'" + value + "', ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(");");
		
		String sql = sb.toString();
		
		return state.executeUpdate(sql);
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
			IOHandler.getInstance().log("DBConnector.finalize", e);
		}
		
		try {
			if(connection != null) {
				connection.close();
			}			
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[DBConnector.finalize]", e);
		}
		IOHandler.getInstance().log("MYSQL Closed");
	}
}

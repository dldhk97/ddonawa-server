package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import utility.IOHandler;

public class DBConnector {
	
//	public static void createConnectionPool() {
//		DBCP.getInstance();
//	}
//	
//	public static boolean isConnected() throws Exception{
//		return DBCP.getInstance().isConnected();
//	}
	
	// sql문과 열 이름을 전달받으면 쿼리 후 해당되는 테이블을 받아옴
	public ArrayList<ArrayList<String>> select(String sql, ArrayList<String> columnNames) throws Exception{
		Statement state = null;
		MyConnection mc = null;
		try {
			mc = DBCP.getInstance().getMyConnection();
			state = mc.getConnection().createStatement();
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
			state.close();
			mc.setBusy(false);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(state != null) {
			state.close();			
		}
		if(mc != null) {
			mc.setBusy(false);
		}
		
		return null;
	}
	
	public int insert(String dbName, String tableName, ArrayList<String> columnNames, ArrayList<String> values) throws Exception{
		if(columnNames.size() != values.size()) {
			throw new Exception("SQL INSERT FAILED:Diff size between columnNames and values");
		}
		
		// 따옴표 처리 ex)[리바이스] LEVI'S 511 슬림핏 청바지_블루(04511-3231)
		for(int i = 0 ; i < values.size() ; i++) {
			String value = values.get(i);
			if(value != null)
				values.set(i, value.replace("'", "''"));
		}
		
		// SQL문 작성
		StringBuilder sb = new StringBuilder("INSERT INTO `" + dbName + "`.`" + tableName + "` (");
		for(String columnName : columnNames) {
			sb.append("`" + columnName + "`, ");
		}
		if(columnNames.size() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(") VALUES (");
		
		for(String value : values) {
			String v = value != null ? "'" + value + "', " : "null, ";
			sb.append(v);
		}
		if(values.size() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(");");
		
		String sql = sb.toString();
		
		// SQL문 실행
		Statement state = null;
		MyConnection mc = null;
		try {
			mc = DBCP.getInstance().getMyConnection();
			state = mc.getConnection().createStatement();
			int result = state.executeUpdate(sql);
			state.close();
			mc.setBusy(false);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(state != null) {
			state.close();
		}
		if(mc != null) {
			mc.setBusy(false);
		}
		
		return 0;
	}
	
	public int delete(String dbName, String tableName, ArrayList<String> columnNames, ArrayList<String> values) throws Exception{
		if(columnNames.size() != values.size()) {
			throw new Exception("SQL INSERT FAILED:Diff size between columnNames and values");
		}
		
		// 따옴표 처리 ex)[리바이스] LEVI'S 511 슬림핏 청바지_블루(04511-3231)
		for(int i = 0 ; i < values.size() ; i++) {
			String value = values.get(i);
			if(value != null)
				values.set(i, value.replace("'", "''"));
		}
		
		// SQL문 작성
		StringBuilder sb = new StringBuilder("DELETE FROM `" + dbName + "`.`" + tableName + "` WHERE ");
		
		for(int i = 0 ; i < columnNames.size() ; i++) {
			sb.append("`" + columnNames.get(i) + "` = '" + values.get(i) + "' AND ");
		}
		if(columnNames.size() > 0) {
			sb.delete(sb.length() - 5, sb.length());
		}
		
		String sql = sb.toString();
		IOHandler.getInstance().log("[DEBUG] DELETE SQL : " + sql);
		
		Statement state = null;
		MyConnection mc = null;
		try {
			mc = DBCP.getInstance().getMyConnection();
			state = mc.getConnection().createStatement();
			int result = state.executeUpdate(sql);
			state.close();
			mc.setBusy(false);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(state != null) {
			state.close();
		}
		if(mc != null) {
			mc.setBusy(false);
		}
		
		// SQL문 실행
		return 0;
	}
	
	// 키 속성, 키 값이 일치하는 항목의 속성, 값을 변경함
	public int update(String dbName, String tableName,  ArrayList<String> keyColumnNames, ArrayList<String> keyValues, ArrayList<String> columnNames, ArrayList<String> values) throws Exception{
		if(keyColumnNames.size() != keyValues.size()) {
			throw new Exception("SQL UPDATE FAILED:Diff size between keyColumnNames and keyValues");
		}
		else if(columnNames.size() != values.size()) {
			throw new Exception("SQL UPDATE FAILED:Diff size between columnNames and values");
		}
		
		// keyValues 따옴표 처리
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
		
		// values 따옴표 처리
		for(int i = 0 ; i < values.size() ; i++) {
			String value = values.get(i);
			values.set(i, value.replace("'", "''"));
		}
		
		// SQL문 작성
		StringBuilder sb = new StringBuilder("UPDATE `" + dbName + "`.`" + tableName + "` SET ");
		
		for(int i = 0 ; i < columnNames.size() ; i++) {
			sb.append("`" + columnNames.get(i) + "` = '" + values.get(i) +"', ");
		}
		if(columnNames.size() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(" WHERE ");
		
		for(int i = 0 ; i < keyColumnNames.size() ; i++) {
			sb.append("`" + keyColumnNames.get(i) + "` = '" + keyValues.get(i) + "' AND ");
		}
		if(keyColumnNames.size() > 0) {
			sb.delete(sb.length() - 5, sb.length());
		}
		
		String sql = sb.toString();
		
		Statement state = null;
		MyConnection mc = null;
		try {
			mc = DBCP.getInstance().getMyConnection();
			state = mc.getConnection().createStatement();
			int result = state.executeUpdate(sql);
			state.close();
			mc.setBusy(false);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(state != null) {
			state.close();
		}
		if(mc != null) {
			mc.setBusy(false);
		}
		// SQL문 실행
		return state.executeUpdate(sql);
	}
	
//	public static void close() {
//		try {
//			if(DBCP.getInstance() != null) {
//				boolean isClosedAll = DBCP.getInstance().closeAllConnection();
//				if(isClosedAll) {
//					IOHandler.getInstance().log("모든 커넥션이 종료되었습니다.");
//				}
//				else {
//					IOHandler.getInstance().log("모든 커넥션을 종료하는데 실패했습니다.");
//				}
//			}			
//		}
//		catch(Exception e) {
//			IOHandler.getInstance().log("[DBConnector.finalize]", e);
//		}
//		IOHandler.getInstance().log("[SYSTEM]MYSQL Closed");
//	}
	
//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//		close();
//	}
}

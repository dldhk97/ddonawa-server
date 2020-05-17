package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Account;

public class AccountManager extends DBManager{

//	@Override
//	public int insert(Object obj) throws Exception {
//		Account account = (Account)obj;
//		
//		// 계정정보 테이블에 추가할 열 정보 배열 생성
//		ArrayList<String> columns = new ArrayList<>(Arrays.asList(
//				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString(), 
//				DBInfo.TABLE_ACCOUNT_COLUMN_PW.toString()
//				));
//		
//		// 계정정보 테이블에 추가할 데이터 정보 배열 생성
//		ArrayList<String> values = new ArrayList<>(
//				Arrays.asList(account.getId(), account.getPw()));
//		
//		// 쿼리
//		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_ACCOUNT.toString(), columns, values);
//		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("[신규 계정] " + account.getId() + ", " + account.getPw() + " 추가됨.");
//		}
//		else {
//			IOHandler.getInstance().log("[신규 계정] " + account.getId() + ", " + account.getPw() + " 추가에 실패함.");
//		}
//		
//		return cnt;
//	}
	
	@Override
	protected  ArrayList<String> getTableColumnsAll(){
		return new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString(),
				DBInfo.TABLE_ACCOUNT_COLUMN_PW.toString()
				));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_ACCOUNT.toString() + "` WHERE `" +
				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
	}
	@Override
	protected Object getModel(ArrayList<ArrayList<String>> received) {
		for(ArrayList<String> row : received) {
			return new Account(row.get(0), row.get(1));
		}
		return null;
	}
	@Override
	protected ArrayList<String> modelToStringArray(Object object){
		Account account = (Account)object;
		return new ArrayList<>(Arrays.asList(
				account.getId(), 
				account.getPw()
				));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_ACCOUNT.toString();
	}

}

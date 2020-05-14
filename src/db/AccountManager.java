package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Account;
import utility.IOHandler;

public class AccountManager extends DBManager{

	@Override
	public Object findByKey(ArrayList<String> keyValues) throws Exception {
		// 계정정보 테이블에서 조회할 열 목록(id)
		ArrayList<String> tableColumns = new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_ACCOUNT_COULMN_ID.toString()
				));
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_ACCOUNT.toString() + "` WHERE `" +
				DBInfo.TABLE_ACCOUNT_COULMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().select(query, tableColumns);		
		for(ArrayList<String> row : result) {
			return new Account(row.get(0), row.get(1));
		}
		return null;
	}

	@Override
	protected int insert(Object obj) throws Exception {
		Account account = (Account)obj;
//		IOHandler.getInstance().log("[카테고리 추가 요청]" + category.getId() + ", " + category.getName());
		
		// 품목정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_ACCOUNT_COULMN_ID.toString(), DBInfo.TABLE_ACCOUNT_COULMN_PW.toString()));
		
		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(account.getId(), account.getPw()));
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_ACCOUNT.toString(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("[신규 계정] " + account.getId() + ", " + account.getPw() + " 추가됨.");
		}
		else {
			IOHandler.getInstance().log("[신규 계정] " + account.getId() + ", " + account.getPw() + " 추가에 실패함.");
		}
		
		return cnt;
	}

}

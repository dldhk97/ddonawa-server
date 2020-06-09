package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Account;

public class AccountManager extends DBManager{
	
	// 사용자명에 문자열이 포함된 열을 반환한다. isExactly는 ==인지 LIKE인지 구분.
	public ArrayList<Account> searchByAccountId(String str) throws Exception {
		// 상품정보 테이블에서 조회할 열 목록(이름, 품목정보_id)
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 따옴표 처리
		String searchStr = str.replace("'", "''");
		
		// 쿼리 생성
		String query = query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_ACCOUNT.toString() + "` WHERE `" +
				DBInfo.TABLE_ACCOUNT_COLUMN_ID.toString() + "` = '" + searchStr + "'";
		
		
		// 쿼리
		ArrayList<ArrayList<String>> receieved = DBConnector.getInstance().select(query, tableColumns);
		
		// 2차원 문자열 배열을 1차원 Account 배열로 변환 후 반환
		return getModelList(receieved);
	}
	
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
	protected ArrayList<Account> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<Account> result = new ArrayList<Account>();
		for(ArrayList<String> row : received) {
			result.add(new Account(row.get(0), row.get(1)));
		}
		return result.size() > 0 ? result : null;
	}
	@Override
	protected ArrayList<String> getValuesFromObject(Object object){
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
	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object){
		Account account = (Account)object;
		return new ArrayList<>(Arrays.asList(
				account.getId()
				));
	}

}

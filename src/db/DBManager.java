package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Account;
import utility.IOHandler;

public abstract class DBManager {
	// 테이블에서 키값으로 탐색, 존재하면 해당되는 객체 반환, 없으면 NULL 반환
	public Object findByKey(ArrayList<String> keyValues) throws Exception{
		// 각 테이블에서 가져올 열 목록 설정
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		replaceToSQLComma(keyValues);
		
		// 쿼리 생성
		String query = getSelectQueryByKeys(keyValues);
		
		// 쿼리
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 결과 반환
		return getModel(received);
	}
	
	// 무조건 insert. 결과 int가 0이면 실패, 0 이상이면 n개 삽입 성공
	public int insert(Object object) throws Exception {
		// 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = getTableColumnsAll();
		
		// 계정정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = modelToStringArray(object);
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), getTableName(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("DB에 추가 성공");
		}
		else {
			IOHandler.getInstance().log("DB에 추가 실패");
		}
		
		return cnt;
	}
	
	
	protected abstract ArrayList<String> getTableColumnsAll();								// 해당 테이블의 모든 열 이름을 가져온다.
	protected abstract String getSelectQueryByKeys(ArrayList<String> keyValues);			// 테이블 조회를 위한 쿼리 문자열 만들어 가져온다.
	protected abstract Object getModel(ArrayList<ArrayList<String>> received);				// 테이블 조회 결과에서 필요한 객체를 만들어 가져온다.
	protected abstract ArrayList<String> modelToStringArray(Object object); 				// 객체를 알맞게 String 배열로 변환하여 가져온다.
	protected abstract String getTableName();
	
	// 콤마 SQL콤마로 수정
	protected void replaceToSQLComma(ArrayList<String> keyValues){
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
	}
}

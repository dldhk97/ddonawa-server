package db;

import java.util.ArrayList;

public abstract class DBManager {
	// 테이블에서 키값으로 탐색, 존재하면 해당되는 객체 반환, 없으면 NULL 반환
	public Object findByKey(ArrayList<String> keyValues) throws Exception{
		// 각 테이블에서 가져올 열 목록 설정
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 콤마 DB에 들어가기 전에 오류나지 않게 수정함.
		replaceToSQLComma(keyValues);
		
		// 쿼리 생성
		String query = getSelectQueryByKeys(keyValues);
		
		// 쿼리
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 결과 반환
		ArrayList<?> result = getModelList(received);
		return result != null ? result.get(0) : null;
	}
	
	// 객체 하나를 무조건 insert. 결과 int가 0이면 실패, 0 이상이면 n개 삽입 성공.
	public int insert(Object object) throws Exception {
		// 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = getTableColumnsAll();
		
		// 계정정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = modelToStringArray(object);
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), getTableName(), columns, values);
		
//		if(cnt > 0) {
//			IOHandler.getInstance().log(getTableName() + " DB에 추가 성공");
//		}
//		else {
//			IOHandler.getInstance().log(getTableName() + " DB에 추가 실패");
//		}
		
		return cnt;
	}
	
	// DB에 이미 있으면 안넣고, 없으면 넣음
	public int insertIfNotExist(Object object) throws Exception {
		ArrayList<String> keyValues = getKeyValuesFromObject(object);
		if(findByKey(keyValues) == null) {
			return insert(object);
		}
		return 0;
	}
	
	// 테이블의 모든 행 가져옴
	public ArrayList<?> getAllRows() throws Exception{
		// 각 테이블에서 가져올 열 목록 설정
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + getTableName() + "`";
		
		// 쿼리
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 결과 반환
		return getModelList(received);
	}
	
	
	protected abstract ArrayList<String> getTableColumnsAll();									// 해당 테이블의 모든 열 이름을 가져온다.
	protected abstract String getSelectQueryByKeys(ArrayList<String> keyValues);				// 테이블 조회를 위한 쿼리 문자열 만들어 가져온다.
	protected abstract ArrayList<?> getModelList(ArrayList<ArrayList<String>> received);	// 테이블 조회 결과 문자열 배열을 알맞은 객체 배열로 만들어 가져온다.
	protected abstract ArrayList<String> modelToStringArray(Object object); 					// 객체를 알맞게 String 배열로 변환하여 가져온다.
	protected abstract String getTableName();													// 클래스 알맞은 테이블명 반환.
	protected abstract ArrayList<String> getKeyValuesFromObject(Object object);					// 클래스에서 키값만 반환
	
	// 콤마 SQL콤마로 수정
	protected void replaceToSQLComma(ArrayList<String> keyValues){
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
	}
}

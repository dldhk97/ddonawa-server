package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Category;
import utility.IOHandler;

public class CategoryManager extends DBManager{
	
	// DB에서 품목 ID로 탐색해서 품목정보 반환. 품목정보의 키는 id 하나 뿐이다.
	@Override
	public Object findByKey(ArrayList<String> keyValues) throws Exception {
		// 품목정보 테이블에서 조회할 열 목록(id, 이름)
		ArrayList<String> tableColumns = new ArrayList<>(
				Arrays.asList(
						DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), 
						DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()
						));
		
		// 따옴표 처리
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_CATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_CATEGORY_COLUMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().select(query, tableColumns);
		
		for(ArrayList<String> row : result) {
			return new Category(row.get(0), row.get(1));
		}
		return null;
	}

	@Override
	protected int insert(Object obj) throws Exception {
		Category category = (Category)obj;
//		IOHandler.getInstance().log("[카테고리 추가 요청]" + category.getId() + ", " + category.getName());
		
		// 품목정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()));
		
		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(category.getId(), category.getName()));
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_CATEGORY.toString(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("신규 카테고리 " + category.getId() + ", " + category.getName() + " 추가됨.");
		}
		else {
			IOHandler.getInstance().log("신규 카테고리 " + category.getId() + ", " + category.getName() + " 추가에 실패함.");
		}
		
		return cnt;
	}
	
	// DB에 해당 품목정보가 없으면 신규 등록
	public boolean insertIfNotExist(Object obj) throws Exception {
		Category category = (Category)obj;
		int cnt = 0;
		
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(category.getId()));
		if(findByKey(keys) == null) {
			cnt = insert(category);
		}
		return cnt > 0 ? true : false;
	}
}

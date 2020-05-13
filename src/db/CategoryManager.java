package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Category;
import utility.IOHandler;

public class CategoryManager extends DBManager{
	
	// DB에서 품목 ID로 탐색해서 품목명 반환
	@Override
	public Object findByKey(String key) throws Exception {
		// 품목정보 테이블에서 탐색할 열은 id
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()));
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_CATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_CATEGORY.toString() + "`.`" + DBInfo.TABLE_CATEGORY_COLUMN_ID.toString() + "` = '" + key + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().Select(query, columns);
		
		for(ArrayList<String> row : result) {
			return row.get(1);
		}
		return null;
	}

	@Override
	public int requestAdd(Object obj) throws Exception {
		Category category = (Category)obj;
		System.out.println("[카테고리 추가 요청]" + category.getId() + ", " + category.getName());
		
		// 품목정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()));
		
		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(category.getId(), category.getName()));
		
		// 쿼리
		int cnt = DBConnector.getInstance().Insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_CATEGORY.toString(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("[SYSTEM]신규 카테고리 " + category.getId() + ", " + category.getName() + "추가됨.");
		}
		else {
			IOHandler.getInstance().log("[SYSTEM]신규 카테고리 " + category.getId() + ", " + category.getName() + "추가에 실패함.");
		}
		
		return cnt;
	}
}

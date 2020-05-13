package db;

import java.util.ArrayList;
import java.util.Arrays;

import utility.IOHandler;

public class CategoryManager {
	
	// DB에서 품목 ID로 탐색해서 품목명 반환
	public String findCategoryById(String categoryId) throws Exception {
		// 품목정보 테이블에서 탐색할 열은 id
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()));
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_CATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_CATEGORY.toString() + "`.`" + DBInfo.TABLE_CATEGORY_COLUMN_ID.toString() + "` = '" + categoryId + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().Select(query, columns);
		
		for(ArrayList<String> row : result) {
			return row.get(1);
		}
		
		return null;
	}
	
	// 품목 신규 추가
	public boolean requestAddCategory(String categoryId, String categoryName) throws Exception {
		System.out.println("[카테고리 추가 요청]" + categoryId + ", " + categoryName);
		
		// 품목정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString()));
		
		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(categoryId, categoryName));
		
		// 쿼리
		int cnt = DBConnector.getInstance().Insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_CATEGORY.toString(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("[SYSTEM]신규 카테고리 " + categoryId + ", " + categoryName + "추가됨.");
			return true;
		}
		else {
			IOHandler.getInstance().log("[SYSTEM]신규 카테고리 " + categoryId + ", " + categoryName + "추가에 실패함.");
			return false;
		}
	}
}

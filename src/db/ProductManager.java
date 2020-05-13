package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Product;
import utility.IOHandler;

public class ProductManager extends DBManager {

	// DB에서 상품명으로 탐색해서 상품명 반환 없으면 NULL
	@Override
	public Object findByKey(String key) throws Exception {
		// 상품정보 테이블에서 탐색할 열은 이름
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()));
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_PRODUCT.toString() + "` WHERE `" +
				DBInfo.TABLE_PRODUCT.toString() + "`.`" + DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString() + "` = '" + key + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().Select(query, columns);
		
		for(ArrayList<String> row : result) {
			return row.get(0);
		}
		return null;
	}

	
	@Override
	public int requestAdd(Object obj) throws Exception {
		Product product = (Product)obj;
		System.out.println("[상품 추가 요청]" + product.getName() + ", " + product.getCategoryId());
		
		// 상품정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()));
		
		// 상품정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(product.getName(), product.getCategoryId()));
		
		// 쿼리
		int cnt = DBConnector.getInstance().Insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_PRODUCT.toString(), columns, values);
		
		if(cnt > 0) {
			IOHandler.getInstance().log("[SYSTEM]신규 상품정보 " + product.getName() + ", " + product.getCategoryId() + "추가됨.");
		}
		else {
			IOHandler.getInstance().log("[SYSTEM]신규 상품정보 " + product.getName() + ", " + product.getCategoryId() + "추가에 실패함.");
		}
		
		return cnt;
	}
}

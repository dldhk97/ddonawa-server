package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Category;
import model.Product;
import utility.IOHandler;

public class ProductManager extends DBManager {

	// DB에서 상품명으로 탐색해서 상품정보 반환 없으면 NULL
	@Override
	public Object findByKey(ArrayList<String> keyValues) throws Exception {
		// 상품정보 테이블에서 조회할 열 목록(이름, 품목정보_id)
		ArrayList<String> tableColumns = new ArrayList<>(
				Arrays.asList(
						DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), 
						DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()
						));
		
		// 따옴표 처리
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_PRODUCT.toString() + "` WHERE `" +
				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString() + "` = '" + keyValues.get(0) + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().select(query, tableColumns);
		
		for(ArrayList<String> row : result) {
			return new Product(row.get(0), row.get(1));
		}
		return null;
	}

	
	@Override
	protected int insert(Object obj) throws Exception {
		Product product = (Product)obj;
//		IOHandler.getInstance().log("[상품 추가 요청]" + product.getName() + ", " + product.getCategoryId());
		
		// 상품정보 테이블에 추가할 열 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList(DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()));
		
		// 상품정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(
				Arrays.asList(product.getName(), product.getCategoryId()));
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_PRODUCT.toString(), columns, values);
		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("[SYSTEM]신규 상품정보 " + product.getName() + ", " + product.getCategoryId() + "추가됨.");
//		}
//		else {
//			IOHandler.getInstance().log("[SYSTEM]신규 상품정보 " + product.getName() + ", " + product.getCategoryId() + "추가에 실패함.");
//		}
		
		return cnt;
	}


	// DB에 해당 상품정보가 없으면 신규 등록
	public boolean insertIfNotExist(Object obj) throws Exception {
		Product product = (Product)obj;
		int cnt = 0;
		
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(product.getName()));
		if(findByKey(keys) == null) {
			cnt = insert(product);
		}
		return cnt > 0 ? true : false;
	}
	

}

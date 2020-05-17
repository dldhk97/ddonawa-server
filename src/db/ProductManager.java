package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Product;

public class ProductManager extends DBManager {
	
	// 문자열이 포함된 상품명을 가진 상품정보의 목록을 가져온다.
	public ArrayList<Product> searchByProductName(String str) throws Exception {
		// 상품정보 테이블에서 조회할 열 목록(이름, 품목정보_id)
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 따옴표 처리
		String searchStr = str.replace("'", "''");
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_PRODUCT.toString() + "` WHERE `" +
				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString() + "` LIKE '%" + searchStr + "%'";
		
		// 쿼리
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 2차원 문자열 배열을 1차원 Product 배열로 변환 후 반환
		return getModelList(received);
	}
	
	@Override
	protected ArrayList<String> getTableColumnsAll() {
		return new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), 
				DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()
				));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_PRODUCT.toString() + "` WHERE `" +
				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString() + "` = '" + keyValues.get(0) + "'";
	}
	@Override
	protected ArrayList<Product> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<Product> result = new ArrayList<Product>();
		for(ArrayList<String> row : received) {
			result.add(new Product(row.get(0), row.get(1)));
		}
		return result.size() > 0 ? result : null;
	}
	@Override
	protected ArrayList<String> modelToStringArray(Object object) {
		Product product = (Product) object;
		return new ArrayList<>(Arrays.asList(
				product.getName(), 
				product.getCategoryId()
				));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_PRODUCT.toString();
	}

	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object) {
		Product product = (Product) object;
		return new ArrayList<>(Arrays.asList(
				product.getName()
				));
	}

}

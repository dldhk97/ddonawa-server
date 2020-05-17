package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Product;

public class ProductManager extends DBManager {
	
	public ArrayList<Product> searchByStr(String str) throws Exception {
		// 상품정보 테이블에서 조회할 열 목록(이름, 품목정보_id)
		ArrayList<String> tableColumns = new ArrayList<>(
				Arrays.asList(
						DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(),
						DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()
						));
		
		// 따옴표 처리
		String searchStr = str.replace("'", "''");
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_PRODUCT.toString() + "` WHERE `" +
				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString() + "` LIKE '%" + searchStr + "%'";
		
		// 쿼리
		ArrayList<ArrayList<String>> receieved = DBConnector.getInstance().select(query, tableColumns);
		ArrayList<Product> result = receieved.size() > 0 ? new ArrayList<Product>() : null;
		
		for(ArrayList<String> row : receieved) {
			result.add(new Product(row.get(0), row.get(1)));
		}
		return result;
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
	protected Object getModel(ArrayList<ArrayList<String>> received) {
		for(ArrayList<String> row : received) {
			return new Product(row.get(0), row.get(1));
		}
		return null;
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

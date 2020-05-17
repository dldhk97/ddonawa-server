package db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import model.Favorite;

public class FavoriteManager extends DBManager {

	@Override
	public int insert(Object obj) throws Exception {
//		Product product = (Product)obj;
//		
//		// 상품정보 테이블에 추가할 열 정보 배열 생성
//		ArrayList<String> columns = new ArrayList<>(Arrays.asList(
//				DBInfo.TABLE_PRODUCT_COLUMN_NAME.toString(), 
//				DBInfo.TABLE_PRODUCT_COLUMN_CATEGORYID.toString()
//				));
//		
//		// 상품정보 테이블에 추가할 데이터 정보 배열 생성
//		ArrayList<String> values = new ArrayList<>(
//				Arrays.asList(product.getName(), product.getCategoryId()));
//		
//		// 쿼리
//		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_PRODUCT.toString(), columns, values);
//		
//		return cnt;
		return 0;
	}
	
	@Override
	protected ArrayList<String> getTableColumnsAll() {
		return new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_FAVORITE_COLUMN_ACCOUNTID.toString(), 
				DBInfo.TABLE_FAVORITE_COLUMN_PRODUCTNAME.toString(),
				DBInfo.TABLE_FAVORITE_COLUMN_TARGETPRICE.toString()
				));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_FAVORITE.toString() + "` WHERE `" +
				DBInfo.TABLE_FAVORITE_COLUMN_ACCOUNTID.toString() + "` = '" + keyValues.get(0) + "' AND `" +
				DBInfo.TABLE_FAVORITE_COLUMN_PRODUCTNAME.toString() + "` = '" + keyValues.get(1) + "'";
	}
	@Override
	protected Object getModel(ArrayList<ArrayList<String>> received) {
		for(ArrayList<String> row : received) {
			String accountId = row.get(0);
			String productName = row.get(1);
			double targetPrice = Double.valueOf(row.get(2));
			
			return new Favorite(accountId, productName, targetPrice);
		}
		return null;
	}

	@Override
	protected ArrayList<String> modelToStringArray(Object object) {
		Favorite favorite = (Favorite) object;
		return new ArrayList<>(Arrays.asList(
				favorite.getAccountId(), 
				favorite.getProductName(),
				String.valueOf(favorite.getTargetPrice())
				));
	}

	@Override
	protected String getTableName() {
		return DBInfo.TABLE_FAVORITE.toString();
	}

}

package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Favorite;

public class FavoriteManager extends DBManager {
	
	// 계정_id가 일치한 찜 목록을 반환한다.
	public ArrayList<Favorite> findByAccountId(String accountId) throws Exception{
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 쿼리 생성. ORDER BY로 가장 최신의 정보를 뽑음.
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_FAVORITE.toString() + "` WHERE `" +
				DBInfo.TABLE_FAVORITE_COLUMN_ACCOUNTID.toString() + "` = '" + accountId + "'";
		
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 2차원 문자열 배열을 1차원 Favorite 배열로 변환 후 반환
		return getModelList(received);
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
	protected ArrayList<Favorite> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<Favorite> result = new ArrayList<Favorite>();
		for(ArrayList<String> row : received) {
			String accountId = row.get(0);
			String productName = row.get(1);
			double targetPrice = Double.valueOf(row.get(2));
			
			result.add(new Favorite(accountId, productName, targetPrice));
		}
		return result.size() > 0 ? result : null;
	}

	@Override
	protected ArrayList<String> getValuesFromObject(Object object) {
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
	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object) {
		Favorite favorite = (Favorite) object;
		return new ArrayList<>(Arrays.asList(
				favorite.getAccountId(), 
				favorite.getProductName()
				));
	}

}

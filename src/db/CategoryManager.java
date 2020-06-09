package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Category;

public class CategoryManager extends DBManager{
	
	// 대품목ID가 일치한 품목정보 목록을 가져온다.
	public ArrayList<Category> findByBigCategoryId(String bigCategoryId) throws Exception{
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 쿼리 생성. ORDER BY로 가장 최신의 정보를 뽑음.
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_CATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_CATEGORY_COLUMN_BIGCATEGORYID.toString() + "` = '" + bigCategoryId + "'";
		
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 2차원 문자열 배열을 1차원 Category 배열로 변환 후 반환
		return getModelList(received);
	}
	
	@Override
	protected ArrayList<String> getTableColumnsAll() {
		return new ArrayList<>(Arrays.asList(
						DBInfo.TABLE_CATEGORY_COLUMN_ID.toString(), 
						DBInfo.TABLE_CATEGORY_COLUMN_NAME.toString(),
						DBInfo.TABLE_CATEGORY_COLUMN_BIGCATEGORYID.toString()
						));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_CATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_CATEGORY_COLUMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
	}
	@Override
	protected ArrayList<Category> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<Category> result = new ArrayList<Category>();
		for(ArrayList<String> row : received) {
			result.add(new Category(row.get(0), row.get(1), row.get(2)));
		}
		return result.size() > 0 ? result : null;
	}
	@Override
	protected ArrayList<String> getValuesFromObject(Object object){
		Category category = (Category) object;
		return new ArrayList<>(Arrays.asList(
				category.getId(), 
				category.getName(),
				category.getBigCategoryId()
				));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_CATEGORY.toString();
	}

	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object) {
		Category category = (Category) object;
		return new ArrayList<>(Arrays.asList(
				category.getId() 
				));
	}
}

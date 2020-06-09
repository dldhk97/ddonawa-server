package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.BigCategory;

public class BigCategoryManager extends DBManager{
	
	@Override
	protected ArrayList<String> getTableColumnsAll(){
		return new ArrayList<>(Arrays.asList(
						DBInfo.TABLE_BIGCATEGORY_COLUMN_ID.toString(), 
						DBInfo.TABLE_BIGCATEGORY_COLUMN_NAME.toString()
						));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_BIGCATEGORY.toString() + "` WHERE `" +
				DBInfo.TABLE_BIGCATEGORY_COLUMN_ID.toString() + "` = '" + keyValues.get(0) + "'";
	}
	@Override
	protected ArrayList<BigCategory> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<BigCategory> result = new ArrayList<BigCategory>();
		for(ArrayList<String> row : received) {
			result.add(new BigCategory(row.get(0), row.get(1)));
		}
		return result.size() > 0 ? result : null;
	}
	@Override
	protected ArrayList<String> getValuesFromObject(Object object){
		BigCategory bigCategory = (BigCategory) object;
		return new ArrayList<>(Arrays.asList(
					bigCategory.getId(), 
					bigCategory.getName()
					));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_BIGCATEGORY.toString();
	}
	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object) {
		BigCategory bigCategory = (BigCategory) object;
		return new ArrayList<>(Arrays.asList(
				bigCategory.getId()
				));
	}
}

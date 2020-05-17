package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.BigCategory;
import utility.IOHandler;

public class BigCategoryManager extends DBManager{

	// DB에 해당 품목정보가 없으면 신규 등록
	public int insertIfNotExist(Object obj) throws Exception {
		BigCategory bigCategory = (BigCategory)obj;
		
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(
				bigCategory.getId()
				));
		if(findByKey(keys) == null) {
			return insert(bigCategory);
		}
		return 0;
	}
	
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
	protected Object getModel(ArrayList<ArrayList<String>> received) {
		for(ArrayList<String> row : received) {
			return new BigCategory(row.get(0), row.get(1));
		}
		return null;
	}
	@Override
	protected ArrayList<String> modelToStringArray(Object object){
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
}

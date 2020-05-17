package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.BigCategory;
import utility.IOHandler;

public class BigCategoryManager extends DBManager{

//	@Override
//	public int insert(Object obj) throws Exception {
//		BigCategory bigCategory = (BigCategory)obj;
//		
//		// 품목정보 테이블에 추가할 열 정보 배열 생성
//		ArrayList<String> columns = new ArrayList<>(Arrays.asList(
//						DBInfo.TABLE_BIGCATEGORY_COLUMN_ID.toString(),
//						DBInfo.TABLE_BIGCATEGORY_COLUMN_NAME.toString()
//						));
//		
//		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
//		ArrayList<String> values = new ArrayList<>(Arrays.asList(
//				bigCategory.getId(), 
//				bigCategory.getName()
//				));
//		
//		// 쿼리
//		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_BIGCATEGORY.toString(), columns, values);
//		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("신규 대품목 " + bigCategory.getId() + ", " + bigCategory.getName() + " 추가됨.");
//		}
//		else {
//			IOHandler.getInstance().log("신규 대품목 " + bigCategory.getId() + ", " + bigCategory.getName() + " 추가에 실패함.");
//		}
//		
//		return cnt;
//	}
	
	// DB에 해당 품목정보가 없으면 신규 등록
	public boolean insertIfNotExist(Object obj) throws Exception {
		BigCategory bigCategory = (BigCategory)obj;
		int cnt = 0;
		
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(
				bigCategory.getId()
				));
		if(findByKey(keys) == null) {
			cnt = insert(bigCategory);
		}
		return cnt > 0 ? true : false;
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

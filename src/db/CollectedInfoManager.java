package db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import model.CollectedInfo;
import utility.IOHandler;

public class CollectedInfoManager extends DBManager {

	// 상품명이 일치한 수집정보 배열 반환. 날짜는 위에서부터 최신순으로 들어있음.
	public ArrayList<CollectedInfo> findByProductName(String productName) throws Exception {
		// 수집정보 테이블에서 조회할 열 목록(상품정보_이름, 수집일자, 가격, URL, 조회수, 썸네일)
		ArrayList<String> tableColumns = getTableColumnsAll();
		
		// 따옴표 처리
		productName = productName.replace("'", "''");
		
		// 쿼리 생성. ORDER BY로 가장 최신의 정보를 뽑음.
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_COLLECTEDINFO.toString() + "` WHERE `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString() + "` = '" + productName + "' ORDER BY `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString() + "` DESC";
		
		// 쿼리
		ArrayList<ArrayList<String>> received = DBConnector.getInstance().select(query, tableColumns);
		
		// 2차원 문자열 배열을 1차원 CollectedInfo 배열로 변환 후 반환
		return getModelList(received);
	}
	
	protected int update(Object obj) throws Exception{
		CollectedInfo collectedInfo = (CollectedInfo)obj;
		
		String productName = collectedInfo.getProductName();
		String collectedDate = collectedInfo.getCollectedDate().toString();
		double price = collectedInfo.getPrice();
		String url = collectedInfo.getUrl();
		long hits = collectedInfo.getHits();
		String thumbnail = collectedInfo.getThumbnail();
		
		// 수집정보 테이블의 키 열 정보 생성
		ArrayList<String> keyColumns = new ArrayList<>(Arrays.asList(
					DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString(), 
					DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString()
					));
		
		// 수집정보 테이블의 키 데이터 정보 배열 생성
		ArrayList<String> keyValues = new ArrayList<>(Arrays.asList(
					productName, 
					collectedDate.toString()
					));
		
		// 선택적으로 추가할 속성들의 속성명과 값 설정
		ArrayList<String> columns = new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRICE.toString(),		// 가격은 NOT NULL
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_HITS.toString()		// 조회수는 NULL이라도 0으로 처리해서 사실상 NOT NULL
				));
		ArrayList<String> values = new ArrayList<>(Arrays.asList(
				String.valueOf(price),
				String.valueOf(hits)
				));
		
		if(url != null) {
			columns.add(DBInfo.TABLE_COLLECTEDINFO_COLUMN_URL.toString());
			values.add(url);
		}
		if(thumbnail != null) {
			columns.add(DBInfo.TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL.toString());
			values.add(thumbnail);
		}
		
		// 쿼리
		int cnt = DBConnector.getInstance().update(DBInfo.DB_NAME.toString(), DBInfo.TABLE_COLLECTEDINFO.toString(),keyColumns, keyValues, columns, values);
		
		return cnt;
	}
	
	// 수집정보가 없으면 insert, 있으면 비교 후 낮은 가격이면 update 
	public boolean upsert(Object obj) throws Exception{
		CollectedInfo newInfo = (CollectedInfo)obj;
		int cnt = 0;
		String productName = newInfo.getProductName();
		String collectedDate = newInfo.getCollectedDate().toString();
		
		// 탐색할 키값은  상품정보_이름과 수집일자
		ArrayList<String> keys = new ArrayList<>(Arrays.asList(
				productName,
				collectedDate
				));
		
		try {
			// 키값(상품명, 날짜)으로 탐색
			CollectedInfo previousInfo = (CollectedInfo) findByKey(keys);
			
			// 수집정보가 존재하지 않는 경우 해당 날짜에 없는 정보이므로 삽입
			if(previousInfo == null) {
				cnt = insert(newInfo);
			}
			else {
				String newThumbnail = newInfo.getThumbnail();
				String prevThumbnail = previousInfo.getThumbnail();
				
				// 이전 수집정보가 존재하면 가격을 비교해서 낮으면 update한다.
				if(previousInfo.getPrice() > newInfo.getPrice()) {		
					// 만약 신규 정보의 썸네일이 없으면 이전 썸네일을 복사한다.
					if(newThumbnail == null ||newThumbnail.isEmpty()) {
						newInfo.setThumbnail(prevThumbnail);
					}
					cnt = update(newInfo);
				}
				else {
					// 이전 정보의 썸네일이 없고 신규 썸넴일이 있으면 신규 썸네일을 복사해서 업데이트한다.
					if(prevThumbnail == null || prevThumbnail.isEmpty()) {
						if(newThumbnail != null && !newThumbnail.isEmpty()) {
							previousInfo.setThumbnail(newThumbnail);
							cnt = update(previousInfo);
						}
					}
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[CollectedInfoManager.upsert]", e);
			IOHandler.getInstance().log("[DEBUG]CollectedInfo : " + productName);
			throw e;
		}
		
		return cnt > 0 ? true : false;
	}
	
	@Override
	protected ArrayList<String> getTableColumnsAll() {
		return new ArrayList<>(Arrays.asList(
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString(), 
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString(),
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRICE.toString(),
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_URL.toString(),
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_HITS.toString(),
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL.toString()
				));
	}
	@Override
	protected String getSelectQueryByKeys(ArrayList<String> keyValues) {
		return "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_COLLECTEDINFO.toString() + "` WHERE `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString() + "` = '" + keyValues.get(0) + "' AND `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString() + "` = '" + keyValues.get(1) + "'";
	}
	@Override
	protected ArrayList<CollectedInfo> getModelList(ArrayList<ArrayList<String>> received) {
		ArrayList<CollectedInfo> result = new ArrayList<CollectedInfo>();
		for(ArrayList<String> row : received) {
			String productName = row.get(0);
			Date collectedDate = Date.valueOf(row.get(1));
			double price = Double.valueOf(row.get(2));
			String url = row.get(3);
			long hits = row.get(4) != null ? Long.parseLong(row.get(4)) : 0;
			String thumbnail = row.get(5);
			
			result.add(new CollectedInfo(productName, collectedDate, price, url, hits, thumbnail));
		}
		return result.size() > 0 ? result : null;
	}
	@Override
	protected ArrayList<String> modelToStringArray(Object object){
		CollectedInfo collectedInfo = (CollectedInfo)object;
		return new ArrayList<>(Arrays.asList(
				collectedInfo.getProductName(), 
				collectedInfo.getCollectedDate().toString(),
				String.valueOf(collectedInfo.getPrice()),
				collectedInfo.getUrl(),
				String.valueOf(collectedInfo.getHits()),
				collectedInfo.getThumbnail()
				));
	}
	@Override
	protected String getTableName() {
		return DBInfo.TABLE_COLLECTEDINFO.toString();
	}

	@Override
	protected ArrayList<String> getKeyValuesFromObject(Object object) {
		CollectedInfo collectedInfo = (CollectedInfo)object;
		return new ArrayList<>(Arrays.asList(
				collectedInfo.getProductName(), 
				collectedInfo.getCollectedDate().toString()
				));
	}
}

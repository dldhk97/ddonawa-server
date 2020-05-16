package db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import model.Category;
import model.CollectedInfo;
import utility.IOHandler;

public class CollectedInfoManager extends DBManager {

	// 수집정보 테이블에서 상품정보_이름, 수집일자로 탐색, 완전히 일치하면 수집정보 반환, 없으면 NULL
	@Override
	public Object findByKey(ArrayList<String> keyValues) throws Exception {
		// 수집정보 테이블에서 조회할 열 목록(상품정보_이름, 수집일자, 가격, URL, 조회수, 썸네일)
		ArrayList<String> tableColumns = new ArrayList<>(Arrays.asList(
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString(), 
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRICE.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_URL.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_HITS.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL.toString()
						));
		
		// 따옴표 처리
		for(int i = 0 ; i < keyValues.size() ; i++) {
			String keyValue = keyValues.get(i);
			keyValues.set(i, keyValue.replace("'", "''"));
		}
		
		// 쿼리 생성
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_COLLECTEDINFO.toString() + "` WHERE `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString() + "` = '" + keyValues.get(0) + "' AND `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString() + "` = '" + keyValues.get(1) + "'";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().select(query, tableColumns);
		
		for(ArrayList<String> row : result) {
			String productName = row.get(0);
			Date collectedDate = Date.valueOf(row.get(1));
			double price = Double.valueOf(row.get(2));
			String url = row.get(3);
			long hits = row.get(4) != null ? Long.parseLong(row.get(4)) : 0;
			String thumbnail = row.get(5);
			
			return new CollectedInfo(productName, collectedDate, price, url, hits, thumbnail);
		}
		return null;
	}
	
	// 상품명으로 탐색하여 가장 최신의 수집정보 반환
	public Object findByProductName(String productName) throws Exception {
		// 수집정보 테이블에서 조회할 열 목록(상품정보_이름, 수집일자, 가격, URL, 조회수, 썸네일)
		ArrayList<String> tableColumns = new ArrayList<>(
				Arrays.asList(
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString(), 
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRICE.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_URL.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_HITS.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL.toString()
						));
		
		// 따옴표 처리
		productName = productName.replace("'", "''");
		
		// 쿼리 생성. ORDER BY로 가장 최신의 정보를 뽑음.
		String query = "SELECT * FROM `" +
				DBInfo.DB_NAME.toString() + "`.`" + DBInfo.TABLE_COLLECTEDINFO.toString() + "` WHERE `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString() + "` = '" + productName + "' ORDER BY `" +
				DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString() + "` DESC";
		
		// 쿼리
		ArrayList<ArrayList<String>> result = DBConnector.getInstance().select(query, tableColumns);
		
		for(ArrayList<String> row : result) {
			productName = row.get(0);
			Date collectedDate = Date.valueOf(row.get(1));
			double price = Double.valueOf(row.get(2));
			String url = row.get(3);
			long hits = row.get(4) != null ? Long.parseLong(row.get(4)) : 0;
			String thumbnail = row.get(5);
			
			return new CollectedInfo(productName, collectedDate, price, url, hits, thumbnail);
		}
		return null;
	}

	@Override
	protected int insert(Object obj) throws Exception {
		CollectedInfo collectedInfo = (CollectedInfo)obj;
//		IOHandler.getInstance().log("[수집정보 추가 요청]" + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString());
		
		String productName = collectedInfo.getProductName();
		String collectedDate = collectedInfo.getCollectedDate().toString();
		double price = collectedInfo.getPrice();
		String url = collectedInfo.getUrl();
		long hits = collectedInfo.getHits();
		String thumbnail = collectedInfo.getThumbnail();
		
		// 수집정보 테이블에 추가할 열(NOTNULL) 정보 배열 생성
		ArrayList<String> columns = new ArrayList<>(Arrays.asList(
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME.toString(), 
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE.toString(),
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_PRICE.toString(),				//가격는 NOTNULL이라 기본생성에 포함시킴
						DBInfo.TABLE_COLLECTEDINFO_COLUMN_HITS.toString()				//조회수는 비어있어도 0으로 넣음.
						));
		
		// 품목정보 테이블에 추가할 데이터 정보 배열 생성
		ArrayList<String> values = new ArrayList<>(Arrays.asList(
						productName, 
						collectedDate.toString(),
						String.valueOf(price),
						String.valueOf(hits)
						));
		
		// NULL이 될 수 있는 속성들은 선택적 추가
		if(url != null) {
			columns.add(DBInfo.TABLE_COLLECTEDINFO_COLUMN_URL.toString());
			values.add(url);
		}
		if(hits != 0) {
			
			
		}
		if(thumbnail != null) {
			columns.add(DBInfo.TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL.toString());
			values.add(thumbnail);
		}
		
		// 쿼리
		int cnt = DBConnector.getInstance().insert(DBInfo.DB_NAME.toString(), DBInfo.TABLE_COLLECTEDINFO.toString(), columns, values);
		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("[SYSTEM]신규 수집정보 " + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString() + " 추가됨.");
//		}
//		else {
//			IOHandler.getInstance().log("[SYSTEM]신규 수집정보 " + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString() + " 추가에 실패함.");
//		}
		
		return cnt;
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
		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("[SYSTEM]신규 수집정보 " + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString() + " 갱신됨.");
//		}
//		else {
//			IOHandler.getInstance().log("[SYSTEM]신규 수집정보 " + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString() + " 갱신에 실패함.");
//		}
		
		return cnt;
	}
	
	// 수집정보가 없으면 insert, 있으면 비교 후 낮은 가격이면 update 
	public boolean upsert(Object obj) throws Exception{
		CollectedInfo collectedInfo = (CollectedInfo)obj;
		int cnt = 0;
		String productName = collectedInfo.getProductName();
		String collectedDate = collectedInfo.getCollectedDate().toString();
		
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
				cnt = insert(collectedInfo);
			}
			else {
				// 이전 수집정보가 존재하면 가격을 비교해서 낮으면 update한다.
				if(previousInfo.getPrice() > collectedInfo.getPrice()) {
//					IOHandler.getInstance().log("[수집정보] " + collectedDate + ", " + productName + ", " + previousInfo.getPrice() + "->" + collectedInfo.getPrice());
					
					// 만약 신규 정보의 썸네일이 없으면 이전 썸네일을 복사한다.
					String thumbnail = collectedInfo.getThumbnail();
					if(thumbnail == null ||thumbnail.isEmpty()) {
						collectedInfo.setThumbnail(previousInfo.getThumbnail());
					}
					cnt = update(collectedInfo);
				}
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[CollectedInfoManager.upsert]", e);
			throw e;
		}
		
//		if(cnt > 0) {
//			IOHandler.getInstance().log("[수집정보] " + collectedInfo.getProductName() + ", " + collectedInfo.getCollectedDate().toString() + " 갱신함.");
//		}
		
		return cnt > 0 ? true : false;
	}

}

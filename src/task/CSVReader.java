package task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Date;
import java.util.ArrayList;

import db.BigCategoryManager;
import db.CategoryManager;
import db.CollectedInfoManager;
import db.DBConnector;
import db.ProductManager;
import model.BigCategory;
import model.CSVProduct;
import model.Category;
import model.CollectedInfo;
import model.Product;
import utility.IOHandler;

public class CSVReader {
	
	// 주어진 경로 내에 존재하는 CSV파일을 읽고 DB에 올린다. 하위 폴더까지는 찾지 않음.
	public void dumpCSV(String path) {
		File dir = new File(path);
		
		try {
			// csv로 끝나는 파일만 가져온다.
			String[] fileList = dir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			});
			
			// 탐색한 모든 .csv 파일에 대하여 파싱
			for(String fileName : fileList) {
				String filePath = null;
				
				// 사용자가 준 경로에 맞게 파일명을 붙여 절대경로로 만듦.
				if(path.endsWith("\\")) {
					filePath = path + fileName;
				}
				else {
					filePath = path + "\\" + fileName;
				}
				
				// 한 CSV 파일 읽기
				ArrayList<CSVProduct> productList = readFile(filePath);
				
				// DB 연결 확인
				if(!DBConnector.getInstance().isConnected()) {
		    		IOHandler.getInstance().log("[CSVReader.dumpCSV]DB에 연결할 수 없음");
					return;
				}
				
				// DB에 업데이트
				int cnt = 0;
				IOHandler.getInstance().log("[공공데이터 DB] " + filePath + " 업데이트 시작(총 " + productList.size() + "개)");
				for(CSVProduct p : productList) {
					update(p);
					
					// 1000항목당 한번씩 알림
					if(cnt % 1000 == 0) {
						IOHandler.getInstance().log("[공공데이터 DB] " + filePath + " 업데이트 중(" + cnt + "/" + productList.size() + ")");
					}
					cnt++;
				}
				IOHandler.getInstance().log("[공공데이터 DB] " + filePath + " 업데이트 완료(총 " + productList.size() + "개)");
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CSVReader.dumpCSV-Unknown",e);
		}
	}
	
	// 하나의 CSV 파일을 읽고 CSVProduct 배열 반환하는 메소드
	private ArrayList<CSVProduct> readFile(String filePath) {
		ArrayList<CSVProduct> result = new ArrayList<CSVProduct>();
		
	    try {
	    	FileInputStream input = new FileInputStream(new File(filePath));
	        CharsetDecoder decoder = Charset.forName("EUC-KR").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(input, decoder);
	        BufferedReader bufferedReader = new BufferedReader( reader );
	        
	        String line = bufferedReader.readLine();							// 헤더부분(첫줄)은 읽고 버린다
	        while( (line = bufferedReader.readLine()) != null ) {
	            String[] splited = line.split(",");
	            String collectedDate = splited[0];
	            String productId = splited[1];
	            String categoryId = splited[2];
	            String categoryName = splited[3];								// 품목명도 일단 파싱함. 처음 보는 품목ID면 품목을 DB에 신규 등록하기 위함임.
	            String productName = splited[4];
	            String price = splited[6];
	            
	            CSVProduct product = new CSVProduct(collectedDate, productId, categoryId, categoryName, productName, price);
	            result.add(product);
	        }
	        bufferedReader.close();
	        IOHandler.getInstance().log("CSV 파싱 완료!");
	    } catch (FileNotFoundException e) {
	        IOHandler.getInstance().log("CSVReader.read-FileNotFound", e);
	    } catch( IOException e ) {
	    	IOHandler.getInstance().log("CSVReader.read-IO",e);
	    } catch(Exception e) {
	    	IOHandler.getInstance().log("CSVReader.read-Unknown",e);
	    }
	    return result;
	}
	
	
	// CSV에서 파싱한 상품을 대품목정보/품목정보/상품정보/수집정보로 분리 후 DB에 업로드한다.
	public void update(CSVProduct csvProduct) throws Exception {
		BigCategory bigCategory = createBigCategory(csvProduct);
		Category category = createCategory(csvProduct);
		Product product = createProduct(csvProduct);
		CollectedInfo collectedInfo = createCollectedinfo(csvProduct);
		
		// 대품목정보 없으면 DB에 등록
		BigCategoryManager bcm = new BigCategoryManager();
		if(!bcm.insertIfNotExist(bigCategory)) {
			return;
		}
		
		// 품목정보 없으면 DB에 등록
		CategoryManager cm = new CategoryManager();
		if(!cm.insertIfNotExist(category)) {
			return;
		}
		
		// 상품정보 없으면 상품정보를 DB에 등록
		ProductManager pm = new ProductManager();
		if(!pm.insertIfNotExist(product)) {
			return;
		}
		
		// 수집정보가 없으면 insert, 있으면 비교 후 낮은 가격이면 update 
		CollectedInfoManager cim = new CollectedInfoManager();
		if(!cim.upsert(collectedInfo)) {
			return;
		}
	}
	
	// CSV에서 추출한 CSVProduct 객체를 Product(상품정보) 객체로 변환
	private Product createProduct(CSVProduct csvProduct) {
		// 상품명과 품목정보id로 상품정보 생성
		String productName = csvProduct.getProductName();
		String categoryId = csvProduct.getCategoryId();
		
		Product product = new Product(productName, categoryId);
		return product;
	}
	
	// CSV에서 추출한 CSVProduct 객체를 CollectedInfo(수집정보) 객체로 변환
	private CollectedInfo createCollectedinfo(CSVProduct csvProduct) {
		String productName = csvProduct.getProductName();
		Date collectedDate = Date.valueOf(csvProduct.getCollectedDate());
		double price = Double.parseDouble(csvProduct.getPrice());
		
		// CSV에서 추출한 정보는 조회수를 0으로 설정
		CollectedInfo collectedInfo = new CollectedInfo(productName, collectedDate, price, null, 0, null);
		return collectedInfo;
	}
	
	// CSV에서 추출한 CSVProduct 객체를 Category(품목정보) 객체로 변환
	private Category createCategory(CSVProduct csvProduct) {
		String categoryId = csvProduct.getCategoryId();
		String categoryName = csvProduct.getCategoryName();
		String bigCategoryId = String.valueOf(categoryId.charAt(0));		// 대품목정보는 품목정보Id의 첫글자임(알파벳)
		
		Category category = new Category(categoryId, categoryName, bigCategoryId);
		return category;
	}
	
	private BigCategory createBigCategory(CSVProduct csvProduct) {
		String categoryId = csvProduct.getCategoryId();
		String bigCategoryId = String.valueOf(categoryId.charAt(0));		// 대품목정보는 품목정보Id의 첫글자임(알파벳)
		String bigCategoryName = "카테고리 " + bigCategoryId;					// 이름은 임시로 카테고리 X 라고 붙여준다.
		
		BigCategory category = new BigCategory(bigCategoryId, bigCategoryName);
		return category;
	}
	
}

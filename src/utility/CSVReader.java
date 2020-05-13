package utility;

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

import db.CategoryManager;
import model.CSVProduct;
import model.Category;
import model.CollectedInfo;
import model.Product;

public class CSVReader {
	
	// 하나의 CSV 파일을 읽고 CSVProduct 배열 반환하는 메소드
	public ArrayList<CSVProduct> readFile(String filePath) {
		FileInputStream input;
		ArrayList<CSVProduct> result = new ArrayList<CSVProduct>();
	    try {
	        input = new FileInputStream(new File(filePath));
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
	        System.out.println("CSV 파싱 완료!");
	    } catch (FileNotFoundException e) {
	        IOHandler.getInstance().log("CSVReader.read-FileNotFound", e);
	    } catch( IOException e ) {
	    	IOHandler.getInstance().log("CSVReader.read-IO",e);
	    } catch(Exception e) {
	    	IOHandler.getInstance().log("CSVReader.read-Unknown",e);
	    }
	    return result;
	}
	
	// 주어진 경로 내에 존재하는 CSV파일을 읽고 DB에 올린다. 하위 폴더까지는 찾지 않음.
	public void dumpCSV(String path) {
		File dir = new File(path);
		
		// csv로 끝나는 파일만 가져온다.
		String[] fileList = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		
		// 탐색한 모든 .csv 파일에 대하여
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
			
			// 여기서 바로 CSVProduct를 Product, CollectedInfo로 분리하고 DB에 올린다??
			System.out.println("파일 덤프 시작 : " + filePath);
			for(CSVProduct p : productList) {
				SplitCSVProduct(p);
				System.out.println(p.toString());
			}
			System.out.println("파일 덤프 완료 : "  + filePath);
		}
	}
	
	
	// CSV에서 파싱한 상품을 상품정보/수집정보로 재생성한다. (품목정보는 신규인 경우 생성)
	public void SplitCSVProduct(CSVProduct csvProduct) {
		// 상품명과 품목정보id로 상품정보 생성
		String productName = csvProduct.getProductName();
		String categoryId = csvProduct.getCategoryId();
		Product product = new Product(productName, categoryId);
		
		// CSV정보를 형변환시켜서 수집정보 생성
		Date collectedDate = Date.valueOf(csvProduct.getCollectedDate());
		double price = Double.parseDouble(csvProduct.getPrice());
		CollectedInfo collectedInfo = new CollectedInfo(productName, collectedDate, price);
		
		// 품목정보 id는 기존 DB에 있는지 조회한다. 없으면 새로 만든다.
		CategoryManager cm = new CategoryManager();
		try {
			if(cm.findCategoryById(categoryId) == null) {
				String categoryName = csvProduct.getCategoryName();
				cm.requestAddCategory(categoryId, categoryName);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CSVReader.SplitCSVProduct", e);
		}
		
	}
}

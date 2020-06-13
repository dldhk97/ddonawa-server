package task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import db.BigCategoryManager;
import db.CategoryManager;
import db.CollectedInfoManager;
import db.DBCP;
import db.ProductManager;
import model.BigCategory;
import model.CSVProduct;
import model.Category;
import model.CollectedInfo;
import model.Product;
import utility.IOHandler;

public class CSVReader {
	
	private static Thread _dumpThread; 
	private static DumpTask _dumpTask;
	
	public void dumpCSVBackground(final String path) {
		_dumpTask = new DumpTask(path);
		
		_dumpThread = new Thread(_dumpTask);
		_dumpThread.start();
	}
	
	public static void abortDump() {
		if(_dumpTask != null) {
			try {
				_dumpTask.abortDump();
				IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 백그라운드 스레드가 종료되기를 기다립니다.");
				_dumpThread.join();
				IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 백그라운드 스레드가 종료되었습니다.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isRunning() {
		if(_dumpTask != null) {
			return _dumpTask.isRunning();
		}
		return false;
	}
}

class DumpTask implements Runnable{
	
	private final String path;
	private boolean abort = false;
	private boolean isRunning = false;
	
	private static final int MAX_THREAD = 2;
	private final ExecutorService executorService;
	private ArrayList<DumpThread> threads = new ArrayList<DumpThread>();
	private ArrayList<Future<Boolean>> threadResults = new ArrayList<>(); 
	
	public DumpTask(final String path) {
		this.path = path;
		executorService = Executors.newFixedThreadPool(MAX_THREAD);
	}
	
	public void abortDump() {
		this.abort = true;
		isRunning = false;
		for(DumpThread dt : threads) {
			dt.abort();
		}
		executorService.shutdown();
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void run() {
		isRunning = true;
		
		// 경로 내 모든 CSV를 (쓰레드)작업에 등록한다.
		boolean isAllSubmited = dumpCSV(path);
		
		if(isAllSubmited) {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 모든 CSV가 작업에 등록되었습니다.");
		}
		else if(abort == true) {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] CSV 작업 등록이 사용자의 요청으로 중단되었습니다.");
		}
		else {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] CSV 작업을 등록하는 도중 오류가 발생하였습니다.");
		}
		
		// 모든 쓰레드 작업이 완료되길 기다리고, 결과 받는다.
		int completed = 0;
		for(Future<Boolean> f : threadResults) {
			try {
				Boolean b = f.get();
				if(b == Boolean.TRUE) {
					completed++;
				}
			} 
			catch (Exception e) {
				IOHandler.getInstance().log("[CSVDumpThread-메인스레드] DumpThread 결과를 기다리는 중 오류 발생!");
				e.printStackTrace();
			}
		}
		
		if(completed == threads.size()) {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 모든 CSV가 DB에 갱신되었습니다.");
		}
		else if(abort == true) {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 사용자의 요청으로 중단되었습니다.");
		}
		else {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] CSV를 갱신하는 도중 오류가 발생하였습니다.");
		}
		
		isRunning = false;
	}
	
	// 주어진 경로 내에 존재하는 CSV파일을 읽고 DB에 올린다. 하위 폴더까지는 찾지 않음.
	public boolean dumpCSV(String path) {
		File dir = new File(path);
		
		try {
			// csv로 끝나는 파일만 가져온다.
			String[] fileList = dir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			});
			
			int submitedCnt = 0;
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
				
				if(abort) {
					IOHandler.getInstance().log("[CSVDumpThread-메인스레드] 사용자의 요청으로 for문 중단");
					return false;
				}
				
				// 스레드에 넣어 분할 작업한다!
				DumpThread dt = new DumpThread(filePath);
				threads.add(dt);
				Future<Boolean> result = executorService.submit(dt);
				
				// 결과를 받을 객체(Future)는 따로 보관한다.
				threadResults.add(result);

				submitedCnt++;
			}
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드] " + path +  " 경로 내 작업 등록 완료(" + submitedCnt + "/" + fileList.length + ")");
			return submitedCnt == fileList.length ? true : false;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("[CSVDumpThread-메인스레드",e);
			e.printStackTrace();
		}
		return false;
	}
	
	
}

class DumpThread implements Callable<Boolean> {
	
	private final String filePath;
	private boolean abort = false;
	
	public DumpThread(final String filePath) {
		this.filePath = filePath;
	}
	
	public void abort() {
		this.abort = true;
	}
	
	@Override
	public Boolean call() throws Exception {
		ArrayList<CSVProduct> productList = null;
		try {
			// 한 CSV 파일 읽기
			productList = readFile(filePath);
		}
		catch (Exception e) {
			IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] 파일 읽는데 실패함.");
			e.printStackTrace();
			return false;
		}
		
		// DB 연결 확인
		if(!DBCP.getInstance().isConnected()) {
    		IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"]DB에 연결할 수 없음");
			return null;
		}
		
		// DB에 업데이트
		int cnt = 0;
		IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] " + filePath + " 업데이트 시작(총 " + productList.size() + "개)");
		
		try {
			for(CSVProduct p : productList) {
				if(abort) {
					IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] 사용자의 요청으로 중단됨.");
					return false;
				}
				update(p);						
				
				// 1000항목당 한번씩 알림
				if(cnt % 1000 == 0) {
					int percentage = (100 * cnt / productList.size());
					IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] " + filePath + " 업데이트 중(" + cnt + "/" + productList.size() + ")" + "(" + percentage + "%)");
				}
				cnt++;
			}
			IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] " + filePath + " 업데이트 완료(총 " + productList.size() + "개)");
		}
		catch (Exception e) {
			e.printStackTrace();
			IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] " + filePath + " 업데이트 도중 오류 발생! 업데이트 중단!");
			return false;
		}
		
		return true;
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
	        
	        String errorLine = "";
	        String line = bufferedReader.readLine();							// 헤더부분(첫줄)은 읽고 버린다
	        while( (line = bufferedReader.readLine()) != null ) {
	        	try {
	        		errorLine = line;
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
	        	catch (Exception e) {
	        		IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] CSV 파일에 이상한 항목이 있어 무시했습니다.\n파일명:" + filePath + "\n에러라인:" + errorLine);
				}
	        }
	        bufferedReader.close();
	    } catch(Exception e) {
	    	IOHandler.getInstance().log("[CSVDumpThread-" + Thread.currentThread().getId() +"] CSVReader.read-Unknown",e);
	    	e.printStackTrace();
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
		bcm.insertIfNotExist(bigCategory);
		
		// 품목정보 없으면 DB에 등록
		CategoryManager cm = new CategoryManager();
		cm.insertIfNotExist(category);
		
		// 상품정보 없으면 상품정보를 DB에 등록
		ProductManager pm = new ProductManager();
		pm.insertIfNotExist(product);
		
		// 수집정보가 없으면 insert, 있으면 비교 후 낮은 가격이면 update 
		CollectedInfoManager cim = new CollectedInfoManager();
		cim.upsert(collectedInfo);
	}
	
	// ----------------------- CSVProduct to Object -----------------------------
	
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

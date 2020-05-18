package parser;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import enums.SeleniumManagerStatus;
import model.CollectedInfo;
import model.Product;
import utility.IOHandler;

// 파싱용 쓰레드 생성 및 관리
public class ParserManager {
	
	// 싱글톤 패턴
	private static ParserManager _instance;
	public static ParserManager getInstance() {
		if(_instance == null)
			_instance = new ParserManager();
		return _instance;
	}

	private final int MAX_THREAD = 2; 		// 2의 배수만
	private ExecutorService executorService;
	
	private ArrayList<SeleniumManager> seleniumManagers = new ArrayList<SeleniumManager>();
	
	// 셀레니움 매니저 미리 로드
	public ParserManager() {
		for(int i = 0 ; i < MAX_THREAD; i++) {
			seleniumManagers.add(new SeleniumManager());
		}
		
		// TODO: 쓰레드 클래스 만들어서 셀레니움 매니저 박아넣어라
		executorService = Executors.newFixedThreadPool(MAX_THREAD, new ThreadFactory ( ){
			 public Thread newThread(Runnable r) {
			 return new Thread(r);
			 }});
	}
	
	// 다나와와 네이버쇼핑에서 상품찾기 파싱 요청을 한다.
	public ArrayList<CollectedInfo> requestParse(Product product) {
		Future<ArrayList<CollectedInfo>> danawaResult = null, naverShopResult = null;
		
		try {
			SeleniumManager sm1 = getFreeSelenium();
			if(sm1 == null) {
				//wait for freed selenium manager
			}
			
			ParserThread danawaThread = new ParserThread(product, new DanawaParser(), sm1);
			danawaResult = executorService.submit(danawaThread);
			
			SeleniumManager sm2 = getFreeSelenium();
			if(sm2 == null) {
				//wait for freed selenium manager
			}
			
			ParserThread naverShopThread = new ParserThread(product, new DanawaParser(), sm2);
			naverShopResult = executorService.submit(naverShopThread);
		}
		catch(Exception e) {
			IOHandler.getInstance().log("ParserManager.requestParse-submit", e);
		}
		
		try {
			ArrayList<CollectedInfo> danawaParsed = danawaResult.get();
			ArrayList<CollectedInfo> naverShopParsed = naverShopResult.get();
			if(danawaParsed != null && naverShopParsed != null) {
				danawaParsed.addAll(naverShopParsed);
				return danawaParsed;
			}
			else {
				return danawaParsed != null ? danawaParsed : naverShopParsed;
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("ParserManager.requestParse-get", e);
		}
		return null;
	}
	
	private SeleniumManager getFreeSelenium() {
		for(int i = 0 ; i < seleniumManagers.size() ; i++) {
			SeleniumManager sm = seleniumManagers.get(i);
			if(sm.getStatus() == SeleniumManagerStatus.FREE) {
				return sm;
			}
		}
		return null;
	}
	
	public void close() {
		requestCloseSeleniumManager();
		if(!executorService.isShutdown() || !executorService.isTerminated()) {
			executorService.shutdown();
		}
	}
	
	private void requestCloseSeleniumManager() {
		for(SeleniumManager sm : seleniumManagers) {
			if(sm.isDriverAlive()) {
				sm.quit();
			}
		}
	}

}

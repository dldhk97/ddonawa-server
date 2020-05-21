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

	private final int MAX_THREAD = 8; 		// 2의 배수만
	private ExecutorService executorService;
	private ArrayList<SeleniumManager> seleniumManagerList = new ArrayList<SeleniumManager>();		// 셀레니움 종료를 위해 리스트 가지고있음.
	
	// 셀레니움 매니저 미리 로드
	public ParserManager() {
		// 커스텀 쓰레드 만들어서 셀레니움 매니저 박아넣음.
		executorService = Executors.newFixedThreadPool(MAX_THREAD, new ThreadFactory (){
			public Thread newThread(Runnable r) {
				SeleniumManager sm = new SeleniumManager();
				seleniumManagerList.add(sm);
				CustomThread ct = new CustomThread(r, sm);
				ct.setDaemon(true);
				return ct;
			}});
	}
	
	// 다나와와 네이버쇼핑에서 상품찾기 파싱 요청을 한다.
	public ArrayList<CollectedInfo> requestParse(Product product) {
		Future<ArrayList<CollectedInfo>> danawaResult = null, naverShopResult = null;
		
		try {
			ParserTask danawaTask = new ParserTask(product, new DanawaParser());
			danawaResult = executorService.submit(danawaTask);
			
			ParserTask naverShopThread = new ParserTask(product, new DanawaParser());
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
	
	public void close() {
		for(SeleniumManager sm : seleniumManagerList) {
			if(sm.isDriverAlive()) {
				sm.quit();
			}
		}

		if(!executorService.isShutdown() || !executorService.isTerminated()) {
			executorService.shutdown();
		}
	}

}

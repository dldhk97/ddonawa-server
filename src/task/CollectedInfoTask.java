package task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import db.CollectedInfoManager;
import model.CollectedInfo;
import model.Favorite;
import model.Product;
import model.Tuple;
import network.Response;
import network.ResponseType;
import parser.DanawaParser;
import parser.NaverShopParser;
import parser.ParserManager;
import utility.IOHandler;

public class CollectedInfoTask {
	private final int MIN_CODE_RECOGNIZE_LENGTH = 3;		// n자리 이상부터 A-Za-z0-9로 시작하고 끝나는 단어는 코드로 인식함. 정확성 상승을 위해 사용됨.
	private final double MIN_SIMILAR_PERCENTAGE = 0;		// 유사도가 n 이상은 되어야 고려대상으로 하겠다는 의미.
	
	private final static int COLLECT_TIMEOUT = 5;		// n초 내에 파싱안되면 클라이언트에게 기존 정보 내보냄. 물론 파싱은 계속된다.
	
	// 상품정보로 다나와와 네이버쇼핑 파싱 후 가격 비교, DB에 누적
	public Response collect(Product product) {
		Response response = null;
		try {
			// 디버깅용 시간측정
			long startTime = System.currentTimeMillis();
			
			// 다나와 & 네이버 파싱
			Tuple<ArrayList<CollectedInfo>, ArrayList<CollectedInfo>> received = ParserManager.getInstance().requestParse(product);
			
			if(received == null) {
				IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName() + " 수집 정보 파싱 실패");
				response = new Response(ResponseType.FAILED, "서버에서 수집 정보 파싱에 실패했습니다.");
			}
			
			// 백그라운드 스레드 생성
			UpdateThread ut = new UpdateThread(product, received.getFirst(), received.getSecond());
			
			// 파싱하는데 걸린 시간이 n초 이상이면 백그라운드로 업데이트하고, 클라이언트에게는 기존 정보 반환함.
			long diff = (long) ((System.currentTimeMillis() - startTime) / 1000.0);
			IOHandler.getInstance().log("[DEBUG]파싱 시간 : " + diff + "초");
			if(diff > COLLECT_TIMEOUT) {
				IOHandler.getInstance().log("[DEBUG]파싱 시간 타임아웃으로 비동기로 업데이트 시킵니다.");
				Executors.newFixedThreadPool(1).submit(ut);	// 비동기로 업데이트 시키고 걍 빤스런
				return response = new Response(ResponseType.FAILED, "파싱 시간 타임아웃!");
			}
			
			// 동기로 업데이트
			Response res = ut.call();
			
			// 디버깅용 처리시간 표시
			long endTime = System.currentTimeMillis();
			IOHandler.getInstance().log("[DEBUG]파싱 & 업데이트 처리 시간 : " + (endTime - startTime) / 1000.0 + "초");
			
			return res;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.collect", e);
			response = new Response(ResponseType.ERROR, "수집정보 갱신 중 서버에서 오류가 발생했습니다.");
		}
		
		return response;
	}
	
	class UpdateThread implements Callable<Response>{
		
		private final Product product;
		private final ArrayList<CollectedInfo> danawaParsed;
		private final ArrayList<CollectedInfo> naverShopParsed;
		
		public UpdateThread(Product product, ArrayList<CollectedInfo> danawaParsed, ArrayList<CollectedInfo> naverShopParsed) {
			this.product = product;
			this.danawaParsed = danawaParsed;
			this.naverShopParsed = naverShopParsed;
		}

		@Override
		public Response call() throws Exception {
			try {
				// 정확한 수집정보 선정을 위한 유사도 필터링
				ArrayList<CollectedInfo> danawaFiltered = filtering(product, danawaParsed, true);
				ArrayList<CollectedInfo> naverShopFiltered = filtering(product, naverShopParsed, false);
				
				if(danawaFiltered == null && naverShopFiltered == null) {
					IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName() + " 동일한 제품을 찾는데 실패했습니다!");
					return new Response(ResponseType.SUCCEED, "동일한 제품을 찾는데 실패했습니다!");
				}
				else {
					// 목록 중 가장 저렴한 수집정보 선정
					CollectedInfo mostProperInfo = getMostProper(danawaFiltered, naverShopFiltered);
					
					if(mostProperInfo != null) {
						IOHandler.getInstance().log("[DEBUG]최종 최저가 상품 : " + mostProperInfo.getProductName() + ", 가격 : " + mostProperInfo.getPrice());
						IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName());
						
						// 파싱된 상품명을 DB에 있는 상품명으로 교체 후 DB에 업데이트
						mostProperInfo.setProductName(product.getName());
						CollectedInfoManager cim = new CollectedInfoManager();
						boolean isUpdated = cim.upsert(mostProperInfo);	// DB에 업데이트함. true면 갱신됨, false면 실패 or 가격경쟁 패배
						if(isUpdated) {
							IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName() + " 수집 정보 업데이트 성공!");
							return new Response(ResponseType.SUCCEED, "수집 정보 업데이트 성공!");
						}
						else {
							// 실패한건 아니고 업데이트가 안이루어진거임. 가격 경쟁 패배해서.
							IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName() + " 수집 정보 업데이트 수행되지 않음");
							return new Response(ResponseType.SUCCEED, "수집 정보 업데이트가 수행되지 않았습니다.");
						}
					}
					else {
						IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName() + " 수집정보 업데이트 수행되지 않음!");
						return new Response(ResponseType.SUCCEED, "수집 정보 업데이트가 수행되지 않았습니다.");
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return new Response(ResponseType.FAILED, "수집 정보 업데이트 중 오류 발생!");
		}

	}
	
	public Tuple<Response, ArrayList<CollectedInfo>> findByProduct(Product product) {
		Response response = null;
		
		try {
			CollectedInfoManager cim = new CollectedInfoManager();
			ArrayList<CollectedInfo> result = cim.findByProductName(product.getName());
			
			response = new Response(ResponseType.SUCCEED, "수집 정보 획득 성공");
			return new Tuple<Response, ArrayList<CollectedInfo>>(response, result);
			
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("ConsoleTask.searchCollectedInfo", e);
			response = new Response(ResponseType.ERROR, "수집 정보 획득 중 서버에서 오류가 발생했습니다.");
		}
		
		return new Tuple<Response, ArrayList<CollectedInfo>>(response, null);
	}
	
	// ---------------------------------------------------------
	
	// 수집정보 목록 중 상품명과 비교해서 부정확한 수집정보는 배제한다.
	private ArrayList<CollectedInfo> filtering(Product product, ArrayList<CollectedInfo> infoList, boolean useSimilarFilter) {
		if(infoList == null) {
			return null;
		}
		
		try {
			IOHandler.getInstance().log("[DEBUG]-------------------원본-------------------");
			int debugCnt = 0;
			for(CollectedInfo c : infoList) {
				IOHandler.getInstance().log("[DEBUG]" + debugCnt++ + ". " + c.getProductName()+ ", " + c.getPrice());
			}
			
			// 필터 1 : 상품명에 코드가 있다면, 해당되는 수집정보만 남긴다.
			codeFilter(product, infoList);
			
			IOHandler.getInstance().log("[DEBUG]-------------------1차 필터(코드) 후-------------------");
			debugCnt = 0;
			for(CollectedInfo c : infoList) {
				IOHandler.getInstance().log("[DEBUG]" + debugCnt++ + ". " + c.getProductName()+ ", " + c.getPrice());
			}
			
			// 필터 2 : 유사도를 비교한다. 유사도가 n 이하인 경우에만 남긴다.
			if(useSimilarFilter) {
				simliarFilter(product, infoList);
				
				IOHandler.getInstance().log("[DEBUG]-------------------2차 필터(유사도) 후-------------------");
				debugCnt = 0;
				for(CollectedInfo c : infoList) {
					IOHandler.getInstance().log("[DEBUG]" + debugCnt++ + ". " + c.getProductName()+ ", " + c.getPrice());
				}
			}
			
			return infoList.size() > 0 ? infoList : null;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.filtering", e);
		}
		
		return null;
	}
	
	// 상품명에 코드가 있다면, 수집정보 배열에서 해당 코드가 포함된 수집정보만 남긴다.
	private void codeFilter(Product product, ArrayList<CollectedInfo> infoList){
		String code = findCode(product.getName());
		IOHandler.getInstance().log("[DEBUG] 코드 : " + code);
		
		if(code != null) {
			for(Iterator<CollectedInfo> it = infoList.iterator() ; it.hasNext();) {
				CollectedInfo c = it.next();
				if(!c.getProductName().contains(code)) {
					it.remove();
					IOHandler.getInstance().log("[DEBUG]" + c.getProductName() + " 코드미포함으로 제외됨.");
				}
			}
		}
	}
	
	// 상품명과 수집정보명 두개의 유사도를 비교한다.
	private void simliarFilter(Product product, ArrayList<CollectedInfo> infoList){
		String productName = product.getName().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ");
		
		for(Iterator<CollectedInfo> it = infoList.iterator() ; it.hasNext();) {
			CollectedInfo c = it.next();
			int similarPoint = levenshteinDistance(productName, c.getProductName());
			double similarPercentage = 100 - (similarPoint * 100 / productName.length());
			IOHandler.getInstance().log("[DEBUG]" + c.getProductName() + ", 유사도 점수 : " + similarPoint + ", 유사도 퍼센트 : " + similarPercentage);
			if(similarPercentage < MIN_SIMILAR_PERCENTAGE) {
				it.remove();
			}
			
		}
	}
	
	// 가장 적절한 상품 골라 반환
	private CollectedInfo getMostProper(ArrayList<CollectedInfo> danawaFiltered, ArrayList<CollectedInfo> naverShopFiltered) {
		CollectedInfo danawaFirst = null;
		CollectedInfo naverShopFirst = null;
		if(danawaFiltered != null && danawaFiltered.size() >= 0) {
			danawaFirst = danawaFiltered.get(0);
		}
		
		if(naverShopFiltered != null && naverShopFiltered.size() >= 0) {
			naverShopFirst = naverShopFiltered.get(0);
		}
		
		if(danawaFirst != null && naverShopFirst != null) {
			if(danawaFirst.getPrice() < naverShopFirst.getPrice()) {
				IOHandler.getInstance().log("[DEBUG]다나와 승!");
				return danawaFirst;
			}
			else {
				IOHandler.getInstance().log("[DEBUG]네이버 승!");
				return naverShopFirst;
			}
		}
		else if(danawaFirst != null) {
			IOHandler.getInstance().log("[DEBUG]다나와 부전승!");
			return danawaFirst;
		}
		else {
			IOHandler.getInstance().log("[DEBUG]네이버 부전승!");
			return naverShopFirst;
		}
	}
	
	@Deprecated
	// 가장 저렴한 상품 선택하여 반환
	private CollectedInfo getMostInexpensive(ArrayList<CollectedInfo> infoList) {
		if(infoList == null || infoList.size() <= 0) {
			return null;
		}
		
		CollectedInfo result = infoList.get(0);
		
		for(CollectedInfo c : infoList) {
			if(result.getPrice() > c.getPrice()) {
				result = c;
			}
		}
		return result;
	}
	
	// ----------------------------------------------------------------
	// 문자열 관련 메소드
	
	// 코드란 A-Za-z0-9로 시작하거나 끝나는 연속된 문자열(n자리 이상)을 말한다.(n=MIN_CODE_RECOGNIZE_LENGTH)
	private String findCode(String str) {
		String code = null;
		// 문자열 내 영어 혹은 숫자가 있는 경우
		if(str.matches(".*[A-Za-z0-9].*")) {
			// 괄호는 공백으로 치환하자.
			str = str.replaceAll("[\\[\\](){}]", " ");
			// 공백으로 분리한다.
			for(String word : str.split(" ")) {
				// 코드로 인식 가능한 최소 길이 이상이면 연속적인 영어/숫자인지 확인
				if(word.length() >= MIN_CODE_RECOGNIZE_LENGTH) {
					// 영어/숫자로 시작하고 영어/숫자로 끝나는지 체크 중간에 - _ + . 가 있어도 된다.
					if(word.matches("^[A-Za-z0-9]*-*_*\\+*\\.*[A-Za-z0-9]*$")) {
						// 길이가 긴 녀석을 코드로 쓴다.
						if(code == null) {
							code = word;
						}
						else {
							code = word.length() > code.length() ? word : code;
						}
					}
				}
			}
		}
		return code;
	}
	
	
	// Levenshtein distance으로 유사도를 비교한다. 유사도는 0에 가까울수록 productName과 유사하다는 의미임.
	private int levenshteinDistance(String productName, String parsedName) {
		try {
			String longStr, shortStr;
			if(productName.length() > parsedName.length()) {
				longStr = productName;
				shortStr = parsedName;
			}
			else {
				longStr = parsedName;
				shortStr = productName;
			}
			
			// 유사도 측정 전 특수기호 모두 삭제
			longStr = longStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ");
			shortStr = shortStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ");
			
			int longStrLen = longStr.length() + 1;
			int shortStrLen = shortStr.length() + 1;
			
			int[] cost = new int[longStrLen];
			int[] newCost = new int[longStrLen];
			for (int i = 0 ; i < longStrLen; i++) { cost[i] = i; }
			for (int j = 1; j < shortStrLen ; j++) {
				newCost[0] = j;
				for (int i = 1 ; i < longStrLen ; i++) {
					int match = 0;
					if(longStr.charAt(i - 1) != shortStr.charAt(j - 1)) { match = 1; }
					int replace = cost[i - 1] + match;
					int insert = cost[i] + 1;
					int delete = newCost[i - 1] + 1;
					newCost[i] = Math.min(Math.min(insert, delete), replace);
				}
				// 스위칭
				int[] temp = cost;
				cost = newCost;
				newCost = temp;
			}
			return cost[longStrLen - 1];
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.levenshteinDistance", e);
		}
		return 987654321;
	}
}

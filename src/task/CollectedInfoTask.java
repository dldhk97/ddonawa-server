package task;

import java.util.ArrayList;
import java.util.Iterator;

import db.CollectedInfoManager;
import model.CollectedInfo;
import model.Product;
import parser.DanawaParser;
import parser.NaverShopParser;
import parser.ParserManager;
import utility.IOHandler;

public class CollectedInfoTask {
	private final int MIN_CODE_RECOGNIZE_LENGTH = 4;		// n자리 이상부터 A-Za-z0-9로 시작하고 끝나는 단어는 코드로 인식함. 정확성 상승을 위해 사용됨.
	private final double MIN_SIMILAR_PERCENTAGE = 0;		// 유사도가 n 이상은 되어야 고려대상으로 하겠다는 의미.
	
	// 상품정보로 다나와와 네이버쇼핑 파싱 후 가격 비교, DB에 누적
	public boolean collect(Product product) {
		boolean isUpdated = false;
		try {
			// 디버깅용 시간측정
			long debugStartTime = System.currentTimeMillis();
			
			// 다나와 파싱
			ArrayList<CollectedInfo> received = ParserManager.getInstance().requestParse(product);
			
			// 정확한 수집정보 선정을 위한 유사도 필터링
			received = filtering(product, received);
			
			// 목록 중 가장 저렴한 수집정보 선정
			CollectedInfo mostInexpensiveInfo = getMostInexpensive(received);
			
			if(mostInexpensiveInfo != null) {
				IOHandler.getInstance().log("[DEBUG]최종 최저가 상품 : " + mostInexpensiveInfo.getProductName() + ", 가격 : " + mostInexpensiveInfo.getPrice());
				IOHandler.getInstance().log("[DEBUG]상품명(검색어) : " + product.getName());
				
				// 파싱된 상품명을 DB에 있는 상품명으로 교체 후 DB에 업데이트
				mostInexpensiveInfo.setProductName(product.getName());
				CollectedInfoManager cim = new CollectedInfoManager();
				isUpdated = cim.upsert(mostInexpensiveInfo);	// DB에 업데이트함. true면 갱신됨, false면 실패 or 가격경쟁 패배
			}
			
			// 디버깅용 처리시간 표시
			long debugEndTime = System.currentTimeMillis();
			IOHandler.getInstance().log("[DEBUG]파싱 & 업데이트 처리 시간 : " + (debugEndTime - debugStartTime) / 1000.0 + "초");
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.collect", e);
		}
		
		return isUpdated;
	}
	
	public ArrayList<CollectedInfo> findByProduct(Product product) {
		CollectedInfoManager cim = new CollectedInfoManager();
		try {
			return cim.findByProductName(product.getName());
			
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("ConsoleTask.searchCollectedInfo", e);
		}
		return null;
	}
	
	// ---------------------------------------------------------
	
	// 수집정보 목록 중 상품명과 비교해서 부정확한 수집정보는 배제한다.
	private ArrayList<CollectedInfo> filtering(Product product, ArrayList<CollectedInfo> infoList) {
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
			simliarFilter(product, infoList);
			
			IOHandler.getInstance().log("[DEBUG]-------------------2차 필터(유사도) 후-------------------");
			debugCnt = 0;
			for(CollectedInfo c : infoList) {
				IOHandler.getInstance().log("[DEBUG]" + debugCnt++ + ". " + c.getProductName()+ ", " + c.getPrice());
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
//					IOHandler.getInstance().log("[DEBUG]" + c.getProductName() + " 코드미포함으로 제외됨.");
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
			if(similarPercentage < MIN_SIMILAR_PERCENTAGE) {
				it.remove();
//				IOHandler.getInstance().log("[DEBUG]" + c.getProductName() + "의 유사도 : " + similarPercentage + "% => 유사도 탈락");
			}
//			else {
//				IOHandler.getInstance().log("[DEBUG]" + c.getProductName() + "의 유사도 : " + similarPercentage + "%");
//			}
			
		}
	}
	
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

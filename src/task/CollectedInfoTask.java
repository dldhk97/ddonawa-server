package task;

import java.util.ArrayList;

import db.CollectedInfoManager;
import db.ProductManager;
import model.CollectedInfo;
import model.Product;
import parser.DanawaParser;
import parser.NaverShopParser;
import utility.IOHandler;

public class CollectedInfoTask {
	private final int MIN_CODE_RECOGNIZE_LENGTH = 5;		// n자리 이상부터 연속된 A-Za-z0-9는 코드로 인식함. 정확성 상승을 위해 사용됨.
	
	// 상품정보로 다나와와 네이버쇼핑 파싱 후 가격 비교, DB에 누적
	public boolean collect(Product product) {
		try {
			// 다나와 파싱
			DanawaParser dp = new DanawaParser();
			ArrayList<CollectedInfo> danawaResults = dp.parse(product.getName());
			
			// 네이버 쇼핑에서 파싱
			NaverShopParser nsp = new NaverShopParser();
			ArrayList<CollectedInfo> naverShopResults = nsp.parse(product.getName());
			
			// 정확한 수집정보 선정을 위한 유사도 필터링
			IOHandler.getInstance().log("---------------- 다나와 --------------------");
			danawaResults = excludeInaccurateInfo(product, danawaResults);
			IOHandler.getInstance().log("---------------- 네이버쇼핑 --------------------");
			naverShopResults = excludeInaccurateInfo(product, naverShopResults);
			
			// 목록 중 가장 저렴한 수집정보 선정
			CollectedInfo danawaInfo = getMostInexpensive(danawaResults);
			CollectedInfo naverShopInfo = getMostInexpensive(naverShopResults);
			
			IOHandler.getInstance().log("상품명(검색어) : " + product.getName());
			
			if(danawaInfo != null) {
				IOHandler.getInstance().log("다나와 최저가 상품 : " + danawaInfo.getProductName() + ", 가격 : " + danawaInfo.getPrice());
			}
			if (naverShopInfo != null) {
				IOHandler.getInstance().log("네이버 최저가 상품 : " + naverShopInfo.getProductName() + ", 가격 : " + naverShopInfo.getPrice());
			}
			
			if(danawaInfo == null && naverShopInfo == null) {
				return false;
			}
			
			// 두 정보 가격 비교 후  최종 수집정보 선정
			CollectedInfo targetInfo;
			if(danawaInfo != null && naverShopInfo != null) {
				targetInfo = danawaInfo.getPrice() > naverShopInfo.getPrice() ? naverShopInfo : danawaInfo;
			} 
			else {
				targetInfo = danawaInfo != null ? danawaInfo : naverShopInfo;
			}
			
			if(targetInfo != null) {
				// 파싱된 상품명을 DB에 있는 상품명으로 교체
				targetInfo.setProductName(product.getName());
				CollectedInfoManager cim = new CollectedInfoManager();
//				cim.upsert(targetInfo);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.collect", e);
		}
		
		return false;
	}
	
	// 수집정보 목록 중 상품명과 비교해서 부정확한 수집정보는 배제한다.
	private ArrayList<CollectedInfo> excludeInaccurateInfo(Product product, ArrayList<CollectedInfo> infoList) {
		if(infoList == null) {
			return null;
		}
		
		ArrayList<CollectedInfo> result = new ArrayList<CollectedInfo>();
		
		// 필터 1 : 상품명에 코드가 있다면, 해당 코드가 있는 수집정보만 남긴다.
		String code = findCode(product.getName());
		if(code != null) {
			for(CollectedInfo c : infoList) {
				if(c.getProductName().contains(code)) {
					result.add(c);
				}
			}
		}
		else {
			result = infoList;
		}
		
		IOHandler.getInstance().log("------------코드(" + code + ") 필터링 이후--------------");
		
		// 필터 2 : 유사도를 비교한다.
		for(CollectedInfo c : result) {
			// 유사도 비교
			int x = levenshteinDistance(product.getName(), c.getProductName());
			IOHandler.getInstance().log("상품명 : " + c.getProductName() + ", 가격 : " + c.getPrice() + ", 유사도 : " + x);
		}
		return result;
	}
	
	// 문자열에서 코드로 인식하는 문자열을 찾음.
	private String findCode(String str) {
		String code = null;
		// 문자열 내 영어 혹은 숫자가 있는 경우
		if(str.matches(".*[A-Za-z0-9].*")) {
			// 공백으로 분리한다.
			
			for(String word : str.split(" ")) {
				// 코드로 인식 가능한 최소 길이 이상이면 연속적인 영어/숫자인지 확인
				if(word.length() >= MIN_CODE_RECOGNIZE_LENGTH) {
					// 영어/숫자로 시작하고 영어/숫자로 끝나는지 체크
					if(word.matches("^[A-Za-z0-9]*-*_*[A-Za-z0-9]*$")) {
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
	
	// 두 String의 유사도 측정 - Levenshtein distance
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
			longStr = longStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
			shortStr = shortStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
			
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

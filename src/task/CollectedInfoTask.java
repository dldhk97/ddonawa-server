package task;

import java.util.ArrayList;

import db.CollectedInfoManager;
import model.CollectedInfo;
import model.Product;
import parser.DanawaParser;
import parser.NaverShopParser;
import parser.Parser;
import utility.IOHandler;

public class CollectedInfoTask {
	// 상품정보로 다나와와 네이버쇼핑 파싱 후 가격 비교, DB에 누적
	public boolean collect(Product product) {
		try {
			// 다나와 파싱
			DanawaParser dp = new DanawaParser();
			CollectedInfo danawaInfo = getMostAccurateInfo(product, dp);
			
			// 네이버쇼핑 파싱
			NaverShopParser nsp = new NaverShopParser();
			CollectedInfo naverShopInfo = getMostAccurateInfo(product, nsp);
			
			// 두 개의 가격 비교
			CollectedInfo targetInfo = danawaInfo.getPrice() >= naverShopInfo.getPrice() ? danawaInfo : naverShopInfo;
			
			// 웹에서 파싱한 상품명은 버리고, 기존 DB의 상품명으로 저장한다.
			targetInfo.setProductName(product.getName());
			
			// 정보가 없으면 insert, 있으면 update
			CollectedInfoManager cim = new CollectedInfoManager();
//			cim.upsert(targetInfo);		//디버깅용으로 막아둠.
			
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CollectedInfoTask.collect", e);
		}
		
		return false;
	}
	
	// 파싱 후 수집정보 목록 중 가장 적합한 수집정보 하나만 가져온다.
	private CollectedInfo getMostAccurateInfo(Product product, Parser parser) {
		// 파서에 알맞게 파싱
		ArrayList<CollectedInfo> infoList = parser.parse(product.getName());
		
		// 결과 분석
		int cnt = 0 ;
		for(CollectedInfo c : infoList) {
			System.out.println(cnt + ". " + c.getProductName());
			cnt++;
		}
		return null;
	}
}

package parser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.TimeoutException;

import utility.IOHandler;

public class DanawaParser extends Parser {
	
    
    private static final String BASE_URL = "http://search.danawa.com/dsearch.php?query=";
	
	@Override
	public void parseTest() {
//		String searchStr = "농심 포스틱(156G)";
		String searchStr = "여성용 겨울 항공 점퍼 R8W1877";
		String orgHtml = "";
		
		try {
			// URL에 한글을 그대로 넣으면 깨질 수 있기 때문에 UTF-8 혹은 EUC-KR로 변환한다.
			String encoded = toUTF8(searchStr);
			
			// 셀레니움으로 크롤링
			SeleniumManager sm = new SeleniumManager();
			orgHtml = sm.crawl(BASE_URL + encoded);
			
			// 필요한 정보 빼내기
			parseProduct(orgHtml);
		}
		catch(TimeoutException te) {
			// 검색 결과가 없거나 타임아웃
			System.out.println("검색 결과가 없거나 타임아웃 발생");
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}
	}
	
	// HTML에서 필요한 정보 빼내기
	private void parseProduct(String html) {
		try {
			// 결과 html 문자열을 document 형태로 변환
			Document doc = Jsoup.parse(html);
			
			// 클래스명으로 선택
			Elements products = doc.getElementsByClass("prod_main_info");
			
			// 여러 항목 중 하나씩 탐색
			for(Element product : products) {
			
				String href = getHref(product);								// 하이퍼링크 추출
				String thumbnailUrl = getThumbnailUrl(product);				// 썸네일 URL 추출
				String productName = getProductName(product);				// 상품명 추출
				String price = getPrice(product);							// 가격 추출
				String ship = getShip(product);								// 배송비 추출
				
				// 등록월, 카테고리는 파싱 안해도 되나? 일단 패스함.
				System.out.println("하이퍼링크 : " + href);
				System.out.println("썸네일 URL : " + thumbnailUrl);
				System.out.println("상품명 : " + productName);
				System.out.println("가격 : " + price);
				System.out.println("배송비 : " + ship);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}	
	}
	
	// ------------------------------ HTML 추출 ------------------------------
	
	// 하이퍼링크 추출
	private String getHref(Element productHtml) {
		Elements hrefElems = productHtml.getElementsByClass("thumb_link");
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
	// 썸네일 URL 추출
	private String getThumbnailUrl(Element productHtml) {
		Elements thumbnailElems = productHtml.getElementsByClass("click_log_product_searched_img_");
		if(thumbnailElems.size() > 0) {
			return thumbnailElems.get(0).attr("src");
		}
		return null;
	}
	
	// 상품명 추출
	private String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass("click_log_product_searched_title_");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		return null;
	}
	
	// 가격 추출
	private String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass("price_sect");
		if(priceElems.size() > 0) {
			return priceElems.get(0).text();
		}
		return null;
	}
	
	// 배송비 추출
	private String getShip(Element productHtml) {
		Elements shipElems = productHtml.getElementsByClass("ship_sect");
		if(shipElems.size() > 0) {
			return shipElems.get(0).text();
		}
		return null;
	}

}

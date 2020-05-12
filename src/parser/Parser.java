package parser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.TimeoutException;

import utility.IOHandler;

public abstract class Parser {
	
	public void parse(String searchStr) {
//		String searchStr = "포스틱(156G)";
		String orgHtml = null;
		SeleniumManager sm = null;
		
		try {
			// URL에 한글을 그대로 넣으면 깨질 수 있기 때문에 UTF-8 혹은 EUC-KR로 변환한다.
			String encoded = toUTF8(searchStr);
			
			// 셀레니움으로 크롤링
			sm = new SeleniumManager();
			orgHtml = sm.explicitCrawl(getBaseUrl() + encoded, getExplicitClassName());
			
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
		
		if(sm != null) {
			sm.quit();			// 웹 드라이버 종료. 나중에 연속해서 파싱할거면 하면 안되고, 만들어 둔 셀레니움 매니저로 계속 crawl 하면됨.
		}
	}
	
	// HTML에서 필요한 정보 빼내기
	protected void parseProduct(String html) {
		try {
			// 결과 html 문자열을 document 형태로 변환
			Document doc = Jsoup.parse(html);
			
			// 클래스명으로 선택
			Elements products = doc.getElementsByClass(getProductClassName());
			
			// 여러 항목 중 하나씩 탐색
			for(Element product : products) {
			
				String href = getHref(product);								// 하이퍼링크 추출
				String thumbnailUrl = getThumbnailUrl(product);				// 썸네일 URL 추출
				String productName = getProductName(product);				// 상품명 추출
				String price = getPrice(product);							// 가격 추출
				
				// 등록월, 카테고리는 파싱 안해도 되나? 일단 패스함.
				System.out.println("하이퍼링크 : " + href);
				System.out.println("썸네일 URL : " + thumbnailUrl);
				System.out.println("상품명 : " + productName);
				System.out.println("가격 : " + price);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}	
	}
	
	// 한글을 UTF-8로 변환하는 메소드
	protected String toUTF8(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	// 자식 클래스에서 처리할 내용들
	protected abstract String getBaseUrl();			// 다나와파서면 다나와의 URL을, 네이버파서면 네이버의 URL을 가져옴 
	protected abstract String getProductClassName();	// 클래스에 맞게 상품을 특정하는 html-className을 가져옴
	protected abstract String getExplicitClassName();
	
	// HTML 파싱
	protected abstract String getHref(Element product);
	protected abstract String getThumbnailUrl(Element product);
	protected abstract String getProductName(Element product);
	protected abstract String getPrice(Element product);

}

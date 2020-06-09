package parser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.TimeoutException;

import model.CollectedInfo;
import utility.IOHandler;

public abstract class Parser {
	
	// 문자열로 다나와/네이버쇼핑에 검색 후 목록 나열함.
	public ArrayList<CollectedInfo> parse(String searchStr, SeleniumManager sm)  {
		String orgHtml = null;
		
		try {
			// URL에 한글을 그대로 넣으면 깨질 수 있기 때문에 UTF-8 혹은 EUC-KR로 변환한다.
			String encoded = toUTF8(searchStr);
			
			// 셀레니움으로 크롤링
			String targetUrl = getBaseUrl() + encoded;
			orgHtml = sm.explicitCrawl(targetUrl, getExplicitClassName());
			
			// 필요한 정보 빼내기
			return parseProduct(orgHtml);
		}
		catch(TimeoutException te) {
			// 검색 결과가 없거나 타임아웃
			System.out.println("검색 결과가 없거나 타임아웃 발생");
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Parser.parse", e);
		}
		
		return null;
	}
	
	// HTML에서 필요한 정보 빼내기
	protected ArrayList<CollectedInfo> parseProduct(String html) {
		ArrayList<CollectedInfo> result = null;
		try {
			// 결과 html 문자열을 document 형태로 변환
			Document doc = Jsoup.parse(html);
			
			// 검색 정확도가 낮은지 체크한다. (네이버쇼핑만 체크함. 다나와는 부정확하면 아예 안뜬다)
			Elements lowAccuracyCheck = doc.getElementsByClass(getLowAccuracyClassName());
			if(isLowAccuracy(lowAccuracyCheck)) {
				return null;
			}
			
			// 클래스명으로 선택
			Elements products = doc.getElementsByClass(getProductClassName());
			if(products.size() > 0){
				result = new ArrayList<CollectedInfo>();
			}
			
			// 여러 항목 중 하나씩 탐색
			for(Element p : products) {
			
				String url = getHref(p);									// 하이퍼링크 추출
				String thumbnail = getThumbnailUrl(p);						// 썸네일 URL 추출
				String productName = getProductName(p);						// 상품명 추출
				String priceStr = getPrice(p).replaceAll("[^0-9]", "");		// 문자열에서 숫자만 추출
				double price = Double.parseDouble(priceStr);				// 가격 추출
				Date date = new Date(Calendar.getInstance().getTime().getTime());		// 오늘 날짜
				
				result.add(new CollectedInfo(productName, date, price, url, 0, thumbnail));
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Parser.parseProduct", e);
		}
		return result;
	}
	
	// 한글을 UTF-8로 변환하는 메소드
	protected String toUTF8(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	// 자식 클래스에서 처리할 내용들
	protected abstract String getBaseUrl();			// 다나와파서면 다나와의 URL을, 네이버파서면 네이버의 URL을 가져옴 
	protected abstract String getProductClassName();	// 클래스에 맞게 상품을 특정하는 html-className을 가져옴
	protected abstract String getExplicitClassName();
    protected abstract String getLowAccuracyClassName();
	
	// HTML 파싱
	protected abstract String getHref(Element product);
	protected abstract String getThumbnailUrl(Element product);
	protected abstract String getProductName(Element product);
	protected abstract String getPrice(Element product);
	
	// 검색 결과가 부정확한지 체크
	protected abstract boolean isLowAccuracy(Elements elems);

}

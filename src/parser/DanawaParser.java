package parser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.CollectedInfo;

public class DanawaParser extends Parser {
	
    private static final String BASE_URL = "http://search.danawa.com/dsearch.php?query=";
    private static final String PRODUCT_CLASS = "prod_main_info";							// 파싱한 HTML에서 상품을 특정하는 클래스명. <div class="prod_main_info">이면 child노드들을 하나의 상품으로 보겠다는 의미.
    private static final String EXPLICIT_CLASS = "product_list";							// 해당 클래스가 로딩될 때 까지 파싱을 하지 않음
    private static final String LOW_ACCURACY_CLASS = "NONE";								// 검색 결과가 모자랄 때 나오는 메소드
    private static final int TIMEOUT = 5;

	// jsoup 파싱 방식. 다나와는 이거씀.
    @Override
    protected ArrayList<CollectedInfo> parseProduct(Document doc){
    	ArrayList<CollectedInfo> result = new ArrayList<CollectedInfo>();
    	
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
		return result;
    }
    
    @Override
    protected String getBaseUrl() {
    	return BASE_URL;
    }
    
    @Override
    protected String getProductClassName() {
    	return PRODUCT_CLASS;
    }
    
    @Override
    protected String getExplicitClassName() {
    	return EXPLICIT_CLASS;
    }
    
    @Override
    protected String getLowAccuracyClassName() {
    	return LOW_ACCURACY_CLASS;
    }
	
	// ------------------------------ HTML 추출 ------------------------------
	
    @Override
	protected String getHref(Element productHtml) {
		Elements hrefElems = productHtml.getElementsByClass("thumb_link");
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
    @Override
	protected String getThumbnailUrl(Element productHtml) {
    	String url = extractThumbnailByClassName(productHtml, "click_log_product_searched_img_");
		if(url == null) {
			url = extractThumbnailByClassName(productHtml, "click_log_product_standard_img_");
		}
		return url;
	}
	
    @Override
	protected String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass("click_log_product_searched_title_");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		
		productNameElems = productHtml.getElementsByClass("click_log_product_standard_title_");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		
		return null;
	}
	
    @Override
	protected String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass("price_sect");
		if(priceElems.size() > 0) {
			return priceElems.get(0).getElementsByTag("strong").get(0).text();
		}
		
		return null;
	}
    
    private String extractThumbnailByClassName(Element productHtml, String className) {
    	Elements thumbnailElems = productHtml.getElementsByClass(className);
		if(thumbnailElems.size() > 0) {
			Element thumbnailElem = thumbnailElems.get(0);
			if(thumbnailElem.hasAttr("data-original")) {
				return thumbnailElem.attr("data-original");
			}
			return thumbnailElem.attr("src");
		}
		return null;
    }

    @Override
    protected int getTimeout() {
    	return TIMEOUT;
    }

}

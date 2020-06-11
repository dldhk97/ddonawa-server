package parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NaverShopParser extends Parser{
	
	private static final String BASE_URL = "https://search.shopping.naver.com/search/all?query=";
	private static final String PRODUCT_CLASS = "basicList_item__2XT81";							// 파싱한 HTML에서 상품을 특정하는 클래스명. <div class="prod_main_info">이면 child노드들을 하나의 상품으로 보겠다는 의미.
	private static final String EXPLICIT_CLASS = "basicList_info_area__17Xyo";							// 해당 클래스가 로딩될 때 까지 파싱을 하지 않음
	private static final String LOW_ACCURACY_CLASS = "right_word partial";					// 검색 결과가 모자랄 때 나오는 메소드
    
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
		Elements hrefElems = productHtml.getElementsByClass("basicList_link__1MaTN");
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
    // 네이버 섬네일 없으면 만들어내야댐
    @Override
	protected String getThumbnailUrl(Element productHtml) {
		Elements thumbnailElems = productHtml.getElementsByClass("thumbnail_thumb__3Agq6");
		if(thumbnailElems.size() > 0) {
			Elements imgElems = thumbnailElems.get(0).getElementsByTag("img");
			if(imgElems.size() > 0) {
				return imgElems.get(0).attr("src");
			};
		}
		return createThumbnailUrl(productHtml);
	}
    
    private String createThumbnailUrl(Element productHtml) {
    	String href = getHref(productHtml);
    	if(href == null) {
    		return null;
    	}
    	try {
    		String splited1 = href.split("nvMid=")[1];
        	String nvMid = splited1.split("&")[0];
        	String head = nvMid.substring(0, 7);
        	String url = "https://shopping-phinf.pstatic.net/main_" + head + "/" + nvMid +".1.jpg?type=f140" ;
        	return url;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
		}
    	
    	return null;
    }
	
    @Override
	protected String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass("basicList_link__1MaTN");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		return null;
	}
	
    @Override
	protected String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass("price_num__2WUXn");
		if(priceElems.size() > 0) {
			return priceElems.get(0).text();
		}
		return null;
	}
    
    @Override
    protected boolean isLowAccuracy(Elements elems) {
    	if(elems.size() > 0) {
    		return true;
    	}
    	return false;
    }
    
    private static final int TIMEOUT = 10;
    @Override
    protected int getTimeout() {
    	return TIMEOUT;
    }
    
    @Override
    protected boolean isNaverShopping() {
    	return true;
    }
}

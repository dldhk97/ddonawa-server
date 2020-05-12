package parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DanawaParser extends Parser {
	
    private static final String BASE_URL = "http://search.danawa.com/dsearch.php?query=";
    private static final String PRODUCT_CLASS = "prod_main_info";							// 파싱한 HTML에서 상품을 특정하는 클래스명. <div class="prod_main_info">이면 child노드들을 하나의 상품으로 보겠다는 의미.
    
    private static final String CLASSNAME_HREF = "thumb_link";									// attr href
    private static final String CLASSNAME_THUMBNAIL_URL = "click_log_product_searched_img_";	// attr src
    private static final String CLASSNAME_PRODUCT_NAME = "click_log_product_searched_title_";	// text
    private static final String CLASSNAME_PRICE = "price_sect";									// text
    
    @Override
    protected String getBaseUrl() {
    	return BASE_URL;
    }
    
    @Override
    protected String getProductClassName() {
    	return PRODUCT_CLASS;
    }
	
	// ------------------------------ HTML 추출 ------------------------------
	
    @Override
	protected String getHref(Element productHtml) {
		Elements hrefElems = productHtml.getElementsByClass(CLASSNAME_HREF);
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
    @Override
	protected String getThumbnailUrl(Element productHtml) {
		Elements thumbnailElems = productHtml.getElementsByClass(CLASSNAME_THUMBNAIL_URL);
		if(thumbnailElems.size() > 0) {
			return thumbnailElems.get(0).attr("src");
		}
		return null;
	}
	
    @Override
	protected String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass(CLASSNAME_PRODUCT_NAME);
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		return null;
	}
	
    @Override
	protected String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass(CLASSNAME_PRICE);
		if(priceElems.size() > 0) {
			return priceElems.get(0).text();
		}
		return null;
	}
	

}

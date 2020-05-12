package parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class NaverShopParser extends Parser{
	
	private static final String BASE_URL = "https://search.shopping.naver.com/search/all.nhn?query=";
	private static final String PRODUCT_CLASS = "_itemSection";							// �Ľ��� HTML���� ��ǰ�� Ư���ϴ� Ŭ������. <div class="prod_main_info">�̸� child������ �ϳ��� ��ǰ���� ���ڴٴ� �ǹ�.
	private static final String EXPLICIT_CLASS = "goods_list";							// �ش� Ŭ������ �ε��� �� ���� �Ľ��� ���� ����
    
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
	
	// ------------------------------ HTML ���� ------------------------------
	
    @Override
	protected String getHref(Element productHtml) {
		Elements hrefElems = productHtml.getElementsByClass("link");
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
    @Override
	protected String getThumbnailUrl(Element productHtml) {
		Elements thumbnailElems = productHtml.getElementsByClass("_productLazyImg");
		if(thumbnailElems.size() > 0) {
			return thumbnailElems.get(0).attr("data-original");
		}
		return null;
	}
	
    @Override
	protected String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass("link");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		return null;
	}
	
    @Override
	protected String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass("num");
		if(priceElems.size() > 0) {
			return priceElems.get(0).text();
		}
		return null;
	}
}

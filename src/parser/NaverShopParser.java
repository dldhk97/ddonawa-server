package parser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import model.CollectedInfo;

public class NaverShopParser extends Parser{
	
	private static final String BASE_URL = "https://search.shopping.naver.com/search/all?query=";
	private static final String PRODUCT_CLASS = "basicList_item__2XT81";							// 파싱한 HTML에서 상품을 특정하는 클래스명. <div class="prod_main_info">이면 child노드들을 하나의 상품으로 보겠다는 의미.
	private static final String EXPLICIT_CLASS = "basicList_item__2XT81";							// 해당 클래스가 로딩될 때 까지 파싱을 하지 않음
	private static final String LOW_ACCURACY_CLASS = "right_word partial";					// 검색 결과가 모자랄 때 나오는 메소드
	private static final int TIMEOUT = 8;
    
	// 네이버 전용
	private static final double MIN_SIMILIARITY = 0.4;
	
	@Override
	protected ArrayList<CollectedInfo> parseProduct(Document doc) {
		// 검색 정확도가 낮은지 체크한다. (네이버쇼핑만 체크함. 다나와는 부정확하면 아예 안뜬다)
		Elements lowAccuracyCheck = doc.getElementsByClass(getLowAccuracyClassName());
		if(lowAccuracyCheck.size() > 0) {
    		return null;
    	}
		
		ArrayList<CollectedInfo> result = new ArrayList<CollectedInfo>();
		Elements elems = doc.select("#__NEXT_DATA__");
		Element temp =  elems.get(0);
		Node b = temp.childNode(0);
		String json = b.toString();
		
		try {
			 JSONParser jsonParser = new JSONParser();
			 JSONObject jsonObj = (JSONObject) jsonParser.parse(json);
			 JSONObject t1 = (JSONObject) jsonObj.get("props");
			 JSONObject t2 = (JSONObject) t1.get("pageProps");
			 JSONObject t3 = (JSONObject) t2.get("initialState");
			 JSONObject t4 = (JSONObject) t3.get("products");
			 JSONArray t5 = (JSONArray) t4.get("list");
			 
			 for(int i = 0 ; i < t5.size() ; i++){
				 JSONObject t6 = (JSONObject) t5.get(i);
				 JSONObject obj = (JSONObject) t6.get("item");
				 
				 // 유사도 0.4 미만이면 안넣음.
				 String similarityStr = (String) obj.get("similarity");
				 if(similarityStr != null) {
					 double similarity = Double.parseDouble(similarityStr);
					 if(similarity < MIN_SIMILIARITY) {
						 continue;
					 }
				 }
				 
				 
				 String productName = (String) obj.get("productName");
				 
				 String priceStr = (String) obj.get("price");
				 priceStr = priceStr.replaceAll("[^0-9]", "");		// 문자열에서 숫자만 추출;
				 double price = Double.parseDouble(priceStr);				// 가격 추출
				 String thumbnail = (String) obj.get("imageUrl");
				 String url = (String) obj.get("mallProductUrl");
				 if(url == null) {
					 url = (String) obj.get("crUrl");
				 }
				 
				 Date date = new Date(Calendar.getInstance().getTime().getTime());		// 오늘 날짜
				 result.add(new CollectedInfo(productName,date,price, url, 0, thumbnail));
			 }
			 return result;
		 } 
		catch (Exception e) {
			 e.printStackTrace();
		 }
		
		return null;
	}
	
	// ------------------------------------------------------------
	
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
    protected int getTimeout() {
    	return TIMEOUT;
    }
}

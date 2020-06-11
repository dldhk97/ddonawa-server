package parser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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
			
			orgHtml = sm.explicitCrawl(targetUrl, getExplicitClassName(), getTimeout());
			
//			System.out.println(orgHtml);
			
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
			
			if(isNaverShopping()) {
				// 검색 정확도가 낮은지 체크한다. (네이버쇼핑만 체크함. 다나와는 부정확하면 아예 안뜬다)
				Elements lowAccuracyCheck = doc.getElementsByClass(getLowAccuracyClassName());
				if(isLowAccuracy(lowAccuracyCheck)) {
					return null;
				}
				
				result = parseProductByJSP(doc);
			}
			else {
				// 구버전 방식. 다나와는 이거씀.
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
			
			
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Parser.parseProduct", e);
		}
		return result;
	}
	
	private ArrayList<CollectedInfo> parseProductByJSP(Document doc) {
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
				 
				 String productName = (String) obj.get("productName");
				 String priceStr = (String) obj.get("price");
				 priceStr = priceStr.replaceAll("[^0-9]", "");		// 문자열에서 숫자만 추출;
				 double price = Double.parseDouble(priceStr);				// 가격 추출
				 String thumbnail = (String) obj.get("imageUrl");
				 String url = (String) obj.get("mallProductUrl");
				 
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
	
	// 한글을 UTF-8로 변환하는 메소드
	protected String toUTF8(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	// 자식 클래스에서 처리할 내용들
	protected abstract String getBaseUrl();			// 다나와파서면 다나와의 URL을, 네이버파서면 네이버의 URL을 가져옴 
	protected abstract String getProductClassName();	// 클래스에 맞게 상품을 특정하는 html-className을 가져옴
	protected abstract String getExplicitClassName();
    protected abstract String getLowAccuracyClassName();
    protected abstract int getTimeout();
	
	// HTML 파싱
	protected abstract String getHref(Element product);
	protected abstract String getThumbnailUrl(Element product);
	protected abstract String getProductName(Element product);
	protected abstract String getPrice(Element product);
	
	// 검색 결과가 부정확한지 체크
	protected abstract boolean isLowAccuracy(Elements elems);
	protected abstract boolean isNaverShopping();

}

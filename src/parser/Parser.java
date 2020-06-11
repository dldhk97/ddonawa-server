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
			
			Document doc = Jsoup.parse(orgHtml);
			
			// 필요한 정보 빼내기
			return parseProduct(doc);
		}
		catch(TimeoutException te) {
			// 검색 결과가 없거나 타임아웃
			String parserName = this.getClass().getName();
			System.out.println(parserName + " : 검색 결과가 없거나 타임아웃 발생");
		}
		catch(Exception e) {
			IOHandler.getInstance().log("Parser.parse", e);
		}
		
		return null;
	}

	
	// HTML에서 필요한 정보 빼내기
	protected abstract ArrayList<CollectedInfo> parseProduct(Document doc);
	
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

}

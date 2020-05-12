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
//		String searchStr = "��� ����ƽ(156G)";
		String searchStr = "������ �ܿ� �װ� ���� R8W1877";
		String orgHtml = "";
		
		try {
			// URL�� �ѱ��� �״�� ������ ���� �� �ֱ� ������ UTF-8 Ȥ�� EUC-KR�� ��ȯ�Ѵ�.
			String encoded = toUTF8(searchStr);
			
			// �����Ͽ����� ũ�Ѹ�
			SeleniumManager sm = new SeleniumManager();
			orgHtml = sm.crawl(BASE_URL + encoded);
			
			// �ʿ��� ���� ������
			parseProduct(orgHtml);
		}
		catch(TimeoutException te) {
			// �˻� ����� ���ų� Ÿ�Ӿƿ�
			System.out.println("�˻� ����� ���ų� Ÿ�Ӿƿ� �߻�");
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}
	}
	
	// HTML���� �ʿ��� ���� ������
	private void parseProduct(String html) {
		try {
			// ��� html ���ڿ��� document ���·� ��ȯ
			Document doc = Jsoup.parse(html);
			
			// Ŭ���������� ����
			Elements products = doc.getElementsByClass("prod_main_info");
			
			// ���� �׸� �� �ϳ��� Ž��
			for(Element product : products) {
			
				String href = getHref(product);								// �����۸�ũ ����
				String thumbnailUrl = getThumbnailUrl(product);				// ����� URL ����
				String productName = getProductName(product);				// ��ǰ�� ����
				String price = getPrice(product);							// ���� ����
				String ship = getShip(product);								// ��ۺ� ����
				
				// ��Ͽ�, ī�װ��� �Ľ� ���ص� �ǳ�? �ϴ� �н���.
				System.out.println("�����۸�ũ : " + href);
				System.out.println("����� URL : " + thumbnailUrl);
				System.out.println("��ǰ�� : " + productName);
				System.out.println("���� : " + price);
				System.out.println("��ۺ� : " + ship);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}	
	}
	
	// ------------------------------ HTML ���� ------------------------------
	
	// �����۸�ũ ����
	private String getHref(Element productHtml) {
		Elements hrefElems = productHtml.getElementsByClass("thumb_link");
		if(hrefElems.size() > 0) {
			return hrefElems.get(0).attr("href");
		}
		return null;
	}
	
	// ����� URL ����
	private String getThumbnailUrl(Element productHtml) {
		Elements thumbnailElems = productHtml.getElementsByClass("click_log_product_searched_img_");
		if(thumbnailElems.size() > 0) {
			return thumbnailElems.get(0).attr("src");
		}
		return null;
	}
	
	// ��ǰ�� ����
	private String getProductName(Element productHtml) {
		Elements productNameElems = productHtml.getElementsByClass("click_log_product_searched_title_");
		if(productNameElems.size() > 0) {
			return productNameElems.get(0).text();
		}
		return null;
	}
	
	// ���� ����
	private String getPrice(Element productHtml) {
		Elements priceElems = productHtml.getElementsByClass("price_sect");
		if(priceElems.size() > 0) {
			return priceElems.get(0).text();
		}
		return null;
	}
	
	// ��ۺ� ����
	private String getShip(Element productHtml) {
		Elements shipElems = productHtml.getElementsByClass("ship_sect");
		if(shipElems.size() > 0) {
			return shipElems.get(0).text();
		}
		return null;
	}

}

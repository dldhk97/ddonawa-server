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
//		String searchStr = "����ƽ(156G)";
		String orgHtml = null;
		SeleniumManager sm = null;
		
		try {
			// URL�� �ѱ��� �״�� ������ ���� �� �ֱ� ������ UTF-8 Ȥ�� EUC-KR�� ��ȯ�Ѵ�.
			String encoded = toUTF8(searchStr);
			
			// �����Ͽ����� ũ�Ѹ�
			sm = new SeleniumManager();
			orgHtml = sm.explicitCrawl(getBaseUrl() + encoded, getExplicitClassName());
			
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
		
		if(sm != null) {
			sm.quit();			// �� ����̹� ����. ���߿� �����ؼ� �Ľ��ҰŸ� �ϸ� �ȵǰ�, ����� �� �����Ͽ� �Ŵ����� ��� crawl �ϸ��.
		}
	}
	
	// HTML���� �ʿ��� ���� ������
	protected void parseProduct(String html) {
		try {
			// ��� html ���ڿ��� document ���·� ��ȯ
			Document doc = Jsoup.parse(html);
			
			// Ŭ���������� ����
			Elements products = doc.getElementsByClass(getProductClassName());
			
			// ���� �׸� �� �ϳ��� Ž��
			for(Element product : products) {
			
				String href = getHref(product);								// �����۸�ũ ����
				String thumbnailUrl = getThumbnailUrl(product);				// ����� URL ����
				String productName = getProductName(product);				// ��ǰ�� ����
				String price = getPrice(product);							// ���� ����
				
				// ��Ͽ�, ī�װ��� �Ľ� ���ص� �ǳ�? �ϴ� �н���.
				System.out.println("�����۸�ũ : " + href);
				System.out.println("����� URL : " + thumbnailUrl);
				System.out.println("��ǰ�� : " + productName);
				System.out.println("���� : " + price);
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log(e.getMessage());
		}	
	}
	
	// �ѱ��� UTF-8�� ��ȯ�ϴ� �޼ҵ�
	protected String toUTF8(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
	
	// �ڽ� Ŭ�������� ó���� �����
	protected abstract String getBaseUrl();			// �ٳ����ļ��� �ٳ����� URL��, ���̹��ļ��� ���̹��� URL�� ������ 
	protected abstract String getProductClassName();	// Ŭ������ �°� ��ǰ�� Ư���ϴ� html-className�� ������
	protected abstract String getExplicitClassName();
	
	// HTML �Ľ�
	protected abstract String getHref(Element product);
	protected abstract String getThumbnailUrl(Element product);
	protected abstract String getProductName(Element product);
	protected abstract String getPrice(Element product);

}

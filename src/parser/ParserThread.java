package parser;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import model.CollectedInfo;
import model.Product;

// 정보를 수집할 상품과, 파서를 정해주면 파싱하는 Callable 객체
public class ParserThread implements Callable<ArrayList<CollectedInfo>>{
	
	private Product targetProduct;
	private Parser parser;
	private SeleniumManager seleniumManager;
	
	public ParserThread(Product targetProduct, Parser parser, SeleniumManager seleniumManager) {
		this.targetProduct = targetProduct;
		this.parser = parser;
		this.seleniumManager = seleniumManager;
	}

	@Override
	public ArrayList<CollectedInfo> call() throws Exception {
		return parser.parse(targetProduct.getName(), seleniumManager);
	}

}
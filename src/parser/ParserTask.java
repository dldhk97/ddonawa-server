package parser;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import model.CollectedInfo;
import model.Product;

// 정보를 수집할 상품과, 파서를 정해주면 파싱하는 Callable 객체
public class ParserTask implements Callable<ArrayList<CollectedInfo>>{
	
	private Product targetProduct;
	private Parser parser;
	
	public ParserTask(Product targetProduct, Parser parser) {
		this.targetProduct = targetProduct;
		this.parser = parser;
	}

	@Override
	public ArrayList<CollectedInfo> call() throws Exception {
		CustomThread currentThread = (CustomThread)Thread.currentThread();
		SeleniumManager seleniumManager = currentThread.getSeleniumManager();
		return parser.parse(targetProduct.getName(), seleniumManager);
	}

}
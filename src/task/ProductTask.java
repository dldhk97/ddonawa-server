package task;

import java.util.ArrayList;

import db.ProductManager;
import model.Product;
import utility.IOHandler;

public class ProductTask {
	public boolean search(String searchWord) {
		try {
			// 문자열 정규화 등 선처리
			
			// SQL에 검색
			ProductManager pm = new ProductManager();
			ArrayList<Product> searchResult = pm.searchByStr(searchWord);
			
			// 결과 반환
			for(Product p : searchResult) {
				System.out.println(p.getName() + ", " + p.getCategoryId());
			}
		}
		catch(Exception e) {
			IOHandler.getInstance().log("ProductTask.search", e);
		}
		return false;
	}
}

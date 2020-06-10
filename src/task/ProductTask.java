package task;

import java.util.ArrayList;
import java.util.Arrays;

import db.ProductManager;
import model.Category;
import model.Product;
import model.Tuple;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class ProductTask {
	
	private final static int SELECT_LIMIT = 1000;
	
	// 사용자가 검색했을 때 동작하는 메소드. 검색하서 결과 상품 목록을 클라이언트에 반환함.
	public Tuple<Response, ArrayList<Product>> searchByProductName(String searchWord) {
		Response response = null;
		try {
			// SQL에 검색
			ProductManager pm = new ProductManager();
			ArrayList<Product> searchResult = pm.searchByProductName(searchWord, SELECT_LIMIT);
			
			response = new Response(ResponseType.SUCCEED, "상품 검색 성공");			
			
			// 결과 반환
			return new Tuple<Response, ArrayList<Product>>(response, searchResult);
		}
		catch(Exception e) {
			IOHandler.getInstance().log("ProductTask.search", e);
			response = new Response(ResponseType.ERROR, "상품 검색 중 서버에서 오류가 발생했습니다.");
		}
		
		return new Tuple<Response, ArrayList<Product>>(response, null);
	}
	
	public Tuple<Response, ArrayList<Product>> searchByCategory(Category category) {
		Response response = null;
		try {
			// SQL에 검색
			ProductManager pm = new ProductManager();
			ArrayList<Product> searchResult = pm.searchByCategory(category, SELECT_LIMIT);
			
			response = new Response(ResponseType.SUCCEED, "상품 검색 성공");			
			
			// 결과 반환
			return new Tuple<Response, ArrayList<Product>>(response, searchResult);
		}
		catch(Exception e) {
			IOHandler.getInstance().log("ProductTask.search", e);
			response = new Response(ResponseType.ERROR, "상품 검색 중 서버에서 오류가 발생했습니다.");
		}
		
		return new Tuple<Response, ArrayList<Product>>(response, null);
	}
	
	public Product getProductByName(String productName){
		try {
			ProductManager pm = new ProductManager();
			ArrayList<String> values = new ArrayList<>(Arrays.asList(
					productName
					));
			Product result = (Product)pm.findByKey(values);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}

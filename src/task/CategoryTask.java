package task;

import java.util.ArrayList;

import db.CategoryManager;
import model.BigCategory;
import model.Category;
import model.Tuple;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class CategoryTask {
	// 사용자가 대품목정보를 클릭했을 때 하위 품목정보를 얻어내는 메소드.
	public Tuple<Response, ArrayList<Category>> findByBigCategory(BigCategory bigCategory) {
		Response response = null;
		
		try {
			// 문자열 정규화 등 선처리. 현재는 별도의 처리 없이 그대로 DB에 SELECT함.
			
			// SQL에 검색
			CategoryManager cm = new CategoryManager();
			ArrayList<Category> categoryList = cm.findByBigCategoryId(bigCategory.getId());
			
			response = new Response(ResponseType.SUCCEED, "분류 가져오는데 성공");
			
			Tuple<Response, ArrayList<Category>> result = new Tuple<Response, ArrayList<Category>>(response, categoryList);
			return result;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CategoryTask.findByBigCategory", e);
			response = new Response(ResponseType.ERROR, "분류를 가져오다 서버에서 에러가 발생했습니다.");
		}
		
		return new Tuple<Response, ArrayList<Category>>(response, null);
	}
}

package task;

import java.util.ArrayList;

import db.CategoryManager;
import model.BigCategory;
import model.Category;
import utility.IOHandler;

public class CategoryTask {
	// 사용자가 대품목정보를 클릭했을 때 하위 품목정보를 얻어내는 메소드.
	public ArrayList<Category> findByBigCategory(BigCategory bigCategory) {
		try {
			// 문자열 정규화 등 선처리. 현재는 별도의 처리 없이 그대로 DB에 SELECT함.
			
			// SQL에 검색
			CategoryManager cm = new CategoryManager();
			ArrayList<Category> received = cm.findByBigCategoryId(bigCategory.getId());
			
			// 결과 반환
			return received;
		}
		catch(Exception e) {
			IOHandler.getInstance().log("CategoryTask.findByBigCategory", e);
		}
		return null;
	}
}

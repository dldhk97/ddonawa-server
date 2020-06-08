package task;

import java.util.ArrayList;

import db.BigCategoryManager;
import model.BigCategory;
import model.Tuple;
import network.Response;
import network.ResponseType;
import utility.IOHandler;

public class BigCategoryTask {
	// 클라이언트가 메인에서 대품목정보 조회할 때 호출됨.
	public Tuple<Response, ArrayList<BigCategory>> getAllBigCategory() {
		Response response = null;
		
		try {
			BigCategoryManager bcm = new BigCategoryManager();
			ArrayList<BigCategory> bigCategoryList = (ArrayList<BigCategory>) bcm.getAllRows();
			
			response = new Response(ResponseType.SUCCEED, "대분류 가져오는데 성공");
			
			Tuple<Response, ArrayList<BigCategory>> result = new Tuple<Response, ArrayList<BigCategory>>(response, bigCategoryList);
			return result;
			
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("BigCategoryTask.getAllBigCategory", e);
			response = new Response(ResponseType.ERROR, "대분류를 가져오다 서버에서 에러가 발생했습니다.");
		}
		
		return new Tuple<Response, ArrayList<BigCategory>>(response, null);
	}
}

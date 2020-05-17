package task;

import java.util.ArrayList;

import db.BigCategoryManager;
import model.BigCategory;
import utility.IOHandler;

public class BigCategoryTask {
	// 클라이언트가 메인에서 대품목정보 조회할 때 호출됨.
	public ArrayList<BigCategory> getAllBigCategory() {
		BigCategoryManager bcm = new BigCategoryManager();
		try {
			ArrayList<BigCategory> received = (ArrayList<BigCategory>) bcm.getAllRows();
			if(received != null) {
				return received;
			}
			
		} 
		catch (Exception e) {
			IOHandler.getInstance().log("BigCategoryTask.getAllBigCategory", e);
		}
		return null;
	}
}

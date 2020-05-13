package db;

import java.util.ArrayList;
import java.util.Arrays;

import model.Category;
import utility.IOHandler;

public class CollectedInfoManager extends DBManager {

	// 키를 String이 아닌 Tuple 혹은 ArrayList로 받아야할듯? 수집정보 테이블의 키가 상품정보_이름과 수집일자, 2개임.
	@Override
	public Object findByKey(String key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int requestAdd(Object obj) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	

}

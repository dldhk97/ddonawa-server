package db;

import java.util.ArrayList;

public abstract class DBManager {
	// 테이블에서 키값으로 탐색, 존재하면 해당되는 객체 반환, 없으면 NULL 반환
	public abstract Object findByKey(ArrayList<String> keyValues) throws Exception;
	
	// 무조건 insert. 결과 int가 0이면 실패, 0 이상이면 n개 삽입 성공
	protected abstract int insert(Object obj) throws Exception;
}

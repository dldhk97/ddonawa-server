package db;

public abstract class DBManager {
	public abstract Object findByKey(String key) throws Exception;
	public abstract int requestAdd(Object obj) throws Exception;
}

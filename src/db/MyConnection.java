package db;

import java.sql.Connection;

public class MyConnection{
	private static int TOTAL_CNT = 0; 
	private final int id;
	private final Connection connection;

	private boolean isBusy = false;
	
	public MyConnection(Connection connection) {
		this.connection = connection;
		this.id = TOTAL_CNT++;
	}
	
	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public int getId() {
		return this.id;
	}
}

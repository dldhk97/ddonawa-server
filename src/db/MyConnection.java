package db;

import java.sql.Connection;

public class MyConnection{
	
	private final Connection connection;

	private boolean isBusy = false;
	
	public MyConnection(Connection connection) {
		this.connection = connection;
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
}

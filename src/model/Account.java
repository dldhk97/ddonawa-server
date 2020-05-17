package model;

import java.io.Serializable;

// DB와 동일한 계정 클래스
public class Account implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String pw;
	
	public Account(String id, String pw) {
		this.id = id;
		this.pw = pw;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPw() {
		return pw;
	}
	
	public void setPw(String pw) {
		this.pw = pw;
	}
	
	@Override
	public String toString() {
		return id + ", " + pw;
	}
	

}

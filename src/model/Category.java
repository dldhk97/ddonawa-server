package model;

import java.io.Serializable;

//DB와 동일한 품목정보 클래스
public class Category implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	
	public Category(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

package model;

import java.io.Serializable;

public class Product implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String categoryId;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}

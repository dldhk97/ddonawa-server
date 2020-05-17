package model;

import java.io.Serializable;

//DB와 동일한 상품정보 클래스
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String categoryId;
	
	public Product(String name, String categoryId) {
		this.name = name;
		this.categoryId = categoryId;
	}
	
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
	
	@Override
	public String toString() {
		return name + ", " + categoryId;
	}
}

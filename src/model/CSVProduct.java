package model;

import java.io.Serializable;

// CSV파일에서 수집한 상품정보(CSV상의 한 줄=row)
public class CSVProduct implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// 일단 CSV에서 가져온 그대로 보관한다. 형변환은 다른클래스에서 하는게 바람직한거 같음.
	private String collectedDate;		// 수집일자 
	private String productId;			// 상품ID
	private String categoryId;			// 품목ID
	private String categoryName;		// 품목명
	private String productName;			// 상품명
	private String price;				// 실가격(discounted price)
	
	public CSVProduct(String collectedDate, String productId, String categoryId, String categoryName, String productName, String price) {
		this.collectedDate = collectedDate;
		this.productId = productId;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.productName = productName;
		this.price = price;
	}
	
	public String toString() {
		return "수집일자:" + collectedDate + ", 상품ID:" + productId + ", 품목ID:" + categoryId + ", 품목명:" + categoryName +  ", 상품명:" + productName + ", 실가격:" + price; 
	}
	
	public String getCollectedDate() {
		return collectedDate;
	}
	
	public void setCollectedDate(String collectedDate) {
		this.collectedDate = collectedDate;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
}

package model;

import java.io.Serializable;

//DB와 동일한 찜 클래스
public class Favorite implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String accountId;
	private String productName;
	private int targetPrice;
	
	public Favorite(String accountId, String productName, int targetPrice) {
		this.accountId = accountId;
		this.productName = productName;
		this.targetPrice = targetPrice;
	}
	
	public String getAccountId() {
		return accountId;
	}
	
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public int getTargetPrice() {
		return targetPrice;
	}
	
	public void setTargetPrice(int targetPrice) {
		this.targetPrice = targetPrice;
	}

}

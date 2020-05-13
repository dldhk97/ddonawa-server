package model;

import java.io.Serializable;

public class Favorite implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String accountId;
	private String productName;
	private int targetPrice;
	
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

package model;

import java.io.Serializable;
import java.sql.Date;

//DB와 동일한 수집정보 클래스
public class CollectedInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String productName;		// 상품정보_이름
	private Date collectedDate;		// 수집일자
	private double price;			// 가격
	private String url;				// URL
	private long hits; 				// 조회수
	private String thumbnail;		// 썸네일
	
//	public CollectedInfo(String productName, Date collectedDate, double price) {
//		this.productName = productName;
//		this.collectedDate = collectedDate;
//		this.price = price;
//	}
	
	public CollectedInfo(String productName, Date collectedDate, double price, String url, long hits, String thumbnail) {
		this.productName = productName;
		this.collectedDate = collectedDate;
		this.price = price;
		this.url = url;
		this.hits = hits;
		this.thumbnail = thumbnail;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public Date getCollectedDate() {
		return collectedDate;
	}
	
	public void setCollectedDate(Date collectedDate) {
		this.collectedDate = collectedDate;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getHits() {
		return hits;
	}
	
	public void setHits(long hits) {
		this.hits = hits;
	}
	
	public String getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
}

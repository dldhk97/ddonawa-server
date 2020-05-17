package db;

public enum DBInfo {
	JDBC_DRIVER("com.mysql.cj.jdbc.Driver"),
	DB_URL("jdbc:mysql://localhost/ddonawa?characterEncoding=UTF-8&serverTimezone=UTC"),
	USER_NAME("ddonawa"),
	PASSWORD("!1q2w3e4r"),
	
	DB_NAME("ddonawa"),
	
	TABLE_ACCOUNT("계정"),
	TABLE_ACCOUNT_COLUMN_ID("id"),
	TABLE_ACCOUNT_COLUMN_PW("pw"),
	
	TABLE_FAVORITE("찜"),
	TABLE_FAVORITE_COLUMN_ACCOUNTID("계정_id"),
	TABLE_FAVORITE_COLUMN_PRODUCTNAME("상품정보_이름"),
	TABLE_FAVORITE_COLUMN_TARGETPRICE("목표금액"),
	
	TABLE_BIGCATEGORY("대품목정보"),
	TABLE_BIGCATEGORY_COLUMN_ID("id"),
	TABLE_BIGCATEGORY_COLUMN_NAME("이름"),
	
	TABLE_CATEGORY("품목정보"),
	TABLE_CATEGORY_COLUMN_ID("id"),
	TABLE_CATEGORY_COLUMN_NAME("이름"),
	TABLE_CATEGORY_COLUMN_BIGCATEGORYID("대품목정보_id"),
	
	TABLE_PRODUCT("상품정보"),
	TABLE_PRODUCT_COLUMN_NAME("이름"),
	TABLE_PRODUCT_COLUMN_CATEGORYID("품목정보_id"),
	
	TABLE_COLLECTEDINFO("수집정보"),
	TABLE_COLLECTEDINFO_COLUMN_PRODUCTNAME("상품정보_이름"),
	TABLE_COLLECTEDINFO_COLUMN_COLLECTEDDATE("수집일자"),
	TABLE_COLLECTEDINFO_COLUMN_PRICE("가격"),
	TABLE_COLLECTEDINFO_COLUMN_URL("URL"),
	TABLE_COLLECTEDINFO_COLUMN_HITS("조회수"),
	TABLE_COLLECTEDINFO_COLUMN_THUMBNAIL("썸네일");
	
	private String val;
	
	private DBInfo(String val) {
		this.val = val;
	}
	
	@Override
	public String toString() {
		return val;
	}
}

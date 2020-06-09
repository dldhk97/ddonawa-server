package network;

import java.io.Serializable;

public enum EventType implements Serializable {
	GET_BIG_CATEGORY,
	GET_CATEGORY, 
	SEARCH, 
	SEARCH_BY_CATEGORY,
	GET_PRODUCT_DETAIL, 
	ADD_FAVORITE, 
	REMOVE_FAVORITE, 
	REQUEST_FAVORITE_CHECK;
}

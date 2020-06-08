package network;

import java.io.Serializable;

public enum EventType implements Serializable {
	GET_BIG_CATEGORY, GET_CATEGORY, SEARCH, GET_PRODUCT_DETAIL, ADD_FAVORITE, REMOVE_FAVORITE, NOTIFY_FAVORITE;
}

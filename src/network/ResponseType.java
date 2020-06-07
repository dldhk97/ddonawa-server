package network;

import java.io.Serializable;

public enum ResponseType implements Serializable {
	SUCCEED, FAILED, ERROR, SERVER_NOT_RESPONSE, UNKNOWN;
}

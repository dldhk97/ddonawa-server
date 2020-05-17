package network;

import java.io.Serializable;

public enum LoginResult implements Serializable{
	SUCCEED, ID_NOT_FOUND, WRONG_PW, ERROR, UNKNOWN;
}

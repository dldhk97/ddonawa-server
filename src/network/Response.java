package network;

import java.io.Serializable;

public class Response implements Serializable {
	private final ResponseType responseType;
	private final String message;
	
	public Response(ResponseType responseType, String message) {
		this.responseType = responseType;
		this.message = message;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public String getMessage() {
		return message;
	}
	
	
}

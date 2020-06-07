package network;

import java.io.Serializable;

public class Response implements Serializable {
	private final ResponseType type;
	private final String message;
	
	public Response(ResponseType type, String message) {
		this.type = type;
		this.message = message;
	}

	public ResponseType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	
	
}

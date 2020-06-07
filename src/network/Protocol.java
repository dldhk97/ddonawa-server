package network;

import java.io.Serializable;

public final class Protocol implements Serializable {
	private final ProtocolType type;
	private final Direction direction;
	private final Object object;

	public Protocol(ProtocolType type, Direction direction, Object object) {
		this.type = type;
		this.direction = direction;
		this.object = object;
	}
	
	public ProtocolType getType() {
		return type;
	}

	public Direction getDirection() {
		return direction;
	}

	public Object getObject() {
		return object;
	}
	
	
}

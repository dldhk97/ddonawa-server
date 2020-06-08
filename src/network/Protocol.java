package network;

import java.io.Serializable;

public final class Protocol implements Serializable {
	private final ProtocolType type;		// 유형(로그인, 회원가입, 이벤트...)
	private final Direction direction;		// 방향
	private final EventType eventType;		// 이벤트라면 이벤트 타입이 있음.
	private final Response response;		// 응답 유형(성공, 실패, 오류)와 메시지
	private final Object object;			// 전달할 데이터

	public Protocol(ProtocolType type, Direction direction, Response response, Object object) {
		this.type = type;
		this.direction = direction;
		this.eventType = null;
		this.response = response;
		this.object = object;
	}
	
	public Protocol(ProtocolType type, Direction direction, EventType eventType, Response response, Object object) {
		this.type = type;
		this.direction = direction;
		this.eventType = eventType;
		this.response = response;
		this.object = object;
	}

	public ProtocolType getType() {
		return type;
	}

	public Direction getDirection() {
		return direction;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public Object getObject() {
		return object;
	}
	
	public Response getResponse() {
		return response;
	}
	
}

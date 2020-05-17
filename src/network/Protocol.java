package network;

public final class Protocol {
	public static class Builder{
		private final ProtocolType type;
		private final Direction direction;
		
		public Builder(ProtocolType type, Direction direction) {
			this.type = type;
			this.direction = direction;
		}
	}
	
	
}

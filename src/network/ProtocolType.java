package network;

public enum ProtocolType {
	ERROR((byte) 0x00), REGISTER((byte) 0x01), LOGIN((byte) 0x02), EVENT((byte) 0x03);

    private final byte code;

    public byte getCode() {
        return code;
    }

    ProtocolType(byte code) {
        this.code = code;
    }

    public static ProtocolType get(byte code) {
        switch (code) {
            case 0x01:
                return REGISTER;
            case 0x02:
                return LOGIN;
            case 0x03:
                return EVENT;
            default:
                return ERROR;
        }
    }
}

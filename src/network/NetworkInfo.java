package network;

public enum NetworkInfo {
	
	SERVER_IP("localhost"),
	SERVER_PORT("9218"),
	SERVER_TIMEOUT("10000");
	
	private String val;

    private NetworkInfo(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}

package parser;

// 셀레니움을 1:1로 가진 스레드
public class CustomThread extends Thread{
	
	private final SeleniumManager seleniumManager;
	
	public CustomThread(Runnable r, final SeleniumManager seleniumManager) {
		super(r);
		this.seleniumManager = seleniumManager;
	}

	public SeleniumManager getSeleniumManager() {
		return seleniumManager;
	}

}

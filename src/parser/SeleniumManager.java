package parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumManager {
	// 크롬으로 크롤링할거면 true로 바꾸고 사용
	private static final boolean CHROME_MODE = true;
	private static final int TIMEOUT_CRWAL = 3;
	
	// 기본은 파폭으로 동작
	public static String WEB_DRIVER_ID = "webdriver.gecko.driver";
	public static String WEB_DRIVER_PATH = ".\\driver\\geckodriver-v0.26.0-win64.exe";
	
	private WebDriver driver;
	
	// 셀레니움 준비	
	public SeleniumManager() {
		if(CHROME_MODE) {
			WEB_DRIVER_ID = "webdriver.chrome.driver";
			WEB_DRIVER_PATH = ".\\driver\\chromedriver_81.0.4044.138_win32.exe";
		}
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        
        //드라이버 설정
        if(CHROME_MODE) {
        	ChromeOptions options = new ChromeOptions();
			options.addArguments("headless");					// 크롬이 화면상 뜨지 않게 함
        	driver = new ChromeDriver(options);
        }
        else {
        	driver = new FirefoxDriver();
        }
	}
	
	// url 던져주면 해당 html 문자열로 반환
	public String crawl(String url) throws Exception{
		// 암시적 대기 (막무가내로 10초 대기 후 로딩)
        //driver.manage().timeouts().implicitlyWait(TIMEOUT_CRWAL, TimeUnit.SECONDS);
		//driver.get(url);
		
		// 명시적 대기 (prod_main_info 클래스가 뜨면 패스, 최대 3초 대기)
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_CRWAL);
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("prod_main_info")));
      
        return driver.getPageSource();
	}
	
	// 이거 안하면 드라이버 프로세스는 살아있음
	public void quit() {
		driver.quit();
	}
	
	@Override
	protected void finalize() throws Throwable {
		quit();
		super.finalize();
	}
}

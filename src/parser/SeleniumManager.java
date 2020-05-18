package parser;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utility.IOHandler;

public class SeleniumManager {
	// 싱글톤이다.
	private static SeleniumManager _instance;
	public static SeleniumManager getInstance()
	{
		if(_instance == null)
			_instance = new SeleniumManager();
		return _instance;
	}
	
	private static final int TIMEOUT_CRWAL = 3;
	
	// 크롬으로 동작
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = ".\\\\driver\\\\chromedriver_81.0.4044.138_win32.exe";
	
	private WebDriver driver;
	
	// 셀레니움 준비	
	public SeleniumManager() {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        
        //드라이버 설정
        ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");					// 크롬이 화면상 뜨지 않게 함
    	driver = new ChromeDriver(options);
	}
	
	// 암시적 대기 후 크롤 (페이지가 로딩되길 기다렸다가 크롤)
	public String implicitCrawl(String url) throws Exception{
        driver.manage().timeouts().implicitlyWait(TIMEOUT_CRWAL, TimeUnit.SECONDS);
		driver.get(url);
      
        return driver.getPageSource();
	}

	// 명시적 대기 후 크롤 (클래스명이 로드될때까지 대기, 클래스가 끝까지 안보이면 예외발생)
	public String explicitCrawl(String url, String className) throws Exception{
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_CRWAL);
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(className)));
      
        return driver.getPageSource();
	}
	
	// 이거 안하면 드라이버 프로세스는 살아있음
	public void quit() {
		if(driver != null) {
			driver.quit();
		}
		IOHandler.getInstance().log("[SYSTEM]Selenium driver Closed");
	}
}

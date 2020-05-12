package parser;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumManager {
	// ũ������ ũ�Ѹ��ҰŸ� true�� �ٲٰ� ���
	private static final boolean CHROME_MODE = true;
	private static final int TIMEOUT_CRWAL = 3;
	
	// �⺻�� �������� ����
	public static String WEB_DRIVER_ID = "webdriver.gecko.driver";
	public static String WEB_DRIVER_PATH = ".\\driver\\geckodriver-v0.26.0-win64.exe";
	
	private WebDriver driver;
	
	// �����Ͽ� �غ�	
	public SeleniumManager() {
		if(CHROME_MODE) {
			WEB_DRIVER_ID = "webdriver.chrome.driver";
			WEB_DRIVER_PATH = ".\\driver\\chromedriver_81.0.4044.138_win32.exe";
		}
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        
        //����̹� ����
        if(CHROME_MODE) {
        	ChromeOptions options = new ChromeOptions();
			options.addArguments("headless");					// ũ���� ȭ��� ���� �ʰ� ��
        	driver = new ChromeDriver(options);
        }
        else {
        	driver = new FirefoxDriver();
        }
	}
	
	// �Ͻ��� ��� �� ũ�� (�������� �ε��Ǳ� ��ٷȴٰ� ũ��)
	public String implicitCrawl(String url) throws Exception{
        driver.manage().timeouts().implicitlyWait(TIMEOUT_CRWAL, TimeUnit.SECONDS);
		driver.get(url);
      
        return driver.getPageSource();
	}

	// ����� ��� �� ũ�� (Ŭ�������� �ε�ɶ����� ���, Ŭ������ ������ �Ⱥ��̸� ���ܹ߻�)
	public String explicitCrawl(String url, String className) throws Exception{
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_CRWAL);
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(className)));
      
        return driver.getPageSource();
	}
	
	// �̰� ���ϸ� ����̹� ���μ����� �������
	public void quit() {
		driver.quit();
	}
}

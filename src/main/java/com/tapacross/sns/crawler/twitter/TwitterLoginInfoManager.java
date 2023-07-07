package com.tapacross.sns.crawler.twitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.SQLNestedException;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.tapacross.sns.crawler.twitter.service.TwitterLoginInfoService;
import com.tapacross.sns.entity.TBTwitterAuthToken;
import com.tapacross.sns.util.ThreadUtil;


public class TwitterLoginInfoManager {
	private WebDriver driver;
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private static final String MAP_TOKEN_KEY_NAME = "crsfToken";
	private static final String MAP_COOKIE_KEY_NAME = "cookie";
	private final Logger logger = LoggerFactory.getLogger(TwitterLoginInfoManager.class);
	private TwitterLoginInfoService service;
	private static int insertCount = 0;
	private static int insertFailCount = 0;
	
	public static void main(String[] args) {
		TwitterLoginInfoManager manger = new TwitterLoginInfoManager();
//		manger.init();
//		manger.selectTrsfAndCookie(id, password)
//		manger.insertNewTwitterId();
//		System.out.println("insertCount : " + insertCount);
//		System.out.println("insertFailCount : " + insertFailCount);
		manger.updateTwitterAuthData();
	}
	
	private void initSpringBeans() {
		@SuppressWarnings("resource")
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:spring/application-context.xml");		
		this.service = context.getBean(TwitterLoginInfoService.class);		
	}
	
	private void init () {
		try {
			this.driver = null;
			if(this.driver == null) {
				ChromeOptions options = new ChromeOptions();
		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
//		        options.addArguments("--incognito");
//		        options.addArguments("--disable-cache");
//		        options.addArguments("--disable-cookies");
				options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
				options.addArguments("--no-sandbox"); // 샌드박스 비활성화
				options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
				options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
				options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
		        options.addArguments("--remote-debugging-address=127.0.0.1");
		        options.addArguments("--remote-debugging-port=9222");
//		        options.addArguments("Sec-Fetch-Site=same-origin");
		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
		        
										        
		        String proxyHost = "121.126.129.148";
		        int proxyPort = 5252;
		        Proxy proxy = new Proxy();
		        proxy.setHttpProxy(proxyHost + ":" + proxyPort);
		        proxy.setSslProxy(proxyHost + ":" + proxyPort);
		        options.addArguments("--proxy-server=http://" + proxyHost + ":" + proxyPort);
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.driver = new ChromeDriver(options);
				} catch (Exception e) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver new ChromeDriver(options Error");
					e.printStackTrace();
					abort();
				}

//				driver.manage().window().maximize(); // 드라이버 초기화 작업
//				driver.manage().deleteAllCookies();
//				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver deleteAllCookies");
//				this.driver = new ChromeDriver();
		        // 현재 브라우저에 저장된 모든 쿠키를 가져옵니다.
//		        Set<Cookie> cookies = driver.manage().getCookies();
//		        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", cookies Check : " + cookies);
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init OK");
//				ThreadUtil.sleepSec(1000);
			}
		} catch (Exception e) {
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver init Exception to occur");
			e.printStackTrace();
			}
	}
	
	private void abort () {
		if (driver != null) {
			driver.close();
			driver.quit();
			driver = null;
		}

	}
	
	/**
	 *  Lock log 찾기 insert fail Lock email
	 */
	public void insertNewTwitterId() {
		try {
			ApplicationContext context = new GenericXmlApplicationContext(
					"classpath:spring/application-context.xml");
			this.service = context.getBean(TwitterLoginInfoService.class);
			String path = context.getBean(ApplicationProperty.class).get("twitter.newId.path");
			File file = new File(path);
			if(!file.exists()) {
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", NOT EXIST TWITTER NEW ID TEXT FILE");
				System.exit(1);
			}
					
			BufferedReader reader;
			reader = new BufferedReader(
			        new FileReader(path)
			    );

			String str;
            while ((str = reader.readLine()) != null) {
            	TBTwitterAuthToken entity = new TBTwitterAuthToken();
            	String[] splitStrings = str.split("\t");
            
            	String email = splitStrings[0];   
            	
            	if (service.selectExistTwitterId(email) != null) {
            		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", exist email in DB :" + email);
            		continue;
            	}
            	
            	String id = splitStrings[1];
            	String password = splitStrings[2];
            	
            	Map<String,String> loginInfo = selectTrsfAndCookie(id, password);
            	
            	if(loginInfo == null) {
            		logger.error(TWITTER_LOGIN_INFO_MANAGER + ", loginInfo is null Pass");
            		logger.error(TWITTER_LOGIN_INFO_MANAGER + ", insert fail Lock email: " + email);
            		insertFailCount ++;
            		continue;
            	}
            	
        		entity.email = email;
        		entity.userId = id;
        		entity.userPassword =password;
        		entity.token =loginInfo.get(MAP_TOKEN_KEY_NAME);
        		entity.cookie =loginInfo.get(MAP_COOKIE_KEY_NAME); 
        		
        		try {
            		service.insertTwitterNewId(entity);   
        		}catch (CannotGetJdbcConnectionException e) {
					e.printStackTrace();
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", insert fail DB Error, email: " + email);
					insertFailCount ++;
				}
 	  
        		insertCount ++;
            } 
            reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void updateTwitterAuthData() {
		initSpringBeans();		
		try {
			List<TBTwitterAuthToken> statusFList = new ArrayList<TBTwitterAuthToken>();
			statusFList = service.selectStatusFTwitterIdList();
			
			if(statusFList.size() == 0) {
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Status F email size 0");
			}
		
			for(TBTwitterAuthToken data : statusFList) {
				String id = data.userId;
				String password = data.userPassword;
				Map<String,String> loginInfo = selectTrsfAndCookie(id,password);
				
				if(loginInfo == null) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", loginInfo is null Pass");
					continue;
				}else {
					TBTwitterAuthToken entity = new TBTwitterAuthToken();
					entity.email = data.email;
					entity.userId = id;
					entity.userPassword =password;
					entity.token =loginInfo.get(MAP_TOKEN_KEY_NAME);
					entity.cookie =loginInfo.get(MAP_COOKIE_KEY_NAME);
					
					try {
						service.updateTwitterAuthData(entity);
					}catch (CannotGetJdbcConnectionException e) {
						e.printStackTrace();
					}

				}
				

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	public Map<String, String> selectTrsfAndCookie(String id, String password){
		Map<String, String> loginInfo = new HashMap<>();
		try {
			String loginUrl = "https://twitter.com/i/flow/login";
			
			if(driver == null) {
				init();
			}
			
			/* login page 이동*/
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver get login start");
			driver.get(loginUrl);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver get login end");
//			ThreadUtil.sleepSec(3);
		
			/* id 입력 */
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start, id : " + id);
			String idXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[5]/label/div/div[2]/div/input";
			WebElement idInput = driver.findElement(By.xpath(idXpath));
			idInput.sendKeys(id);
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start end");	
//			ThreadUtil.sleepSec(3);
			
			/* id 입력 후 클릭 */
			String idLoginInputXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[6]";
			WebElement idLoginInput = driver.findElement(By.xpath(idLoginInputXpath));
			idLoginInput.click();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idLoginInput Click");		
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//			ThreadUtil.sleepSec(3);
			
			/* password 입력*/
			try {				
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pwInput start, password : " + password);
				String pwXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div/div[3]/div/label/div/div[2]/div[1]/input";
				WebElement pwInput = driver.findElement(By.xpath(pwXpath));
				pwInput.sendKeys(password);
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pwInput start end");
//				ThreadUtil.sleepSec(3);
			}catch (NoSuchElementException e) {
				e.printStackTrace();
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check loginInfo id :" + id);
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check pwXpath change");
				return null;
			}
			
			/* password 입력 후 클릭 */
			String pwLoginInputXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div[1]/div/div/div/div";
			WebElement pwLoginInput = driver.findElement(By.xpath(pwLoginInputXpath));
			pwLoginInput.click();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pwLoginInput Click");		
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			ThreadUtil.sleepSec(3);
			
//	        int waitTimeInSeconds = 15;	        
//	        WebDriverWait wait = new WebDriverWait(driver, waitTimeInSeconds);
//
//	        wait.until(new ExpectedCondition<Boolean>() {
//	            @Override
//	            public Boolean apply(WebDriver driver) {
//	                return driver.getCurrentUrl().equals("https://twitter.com/home");
//	            }
//	        });
	        
	        if(driver.getCurrentUrl().equals("https://twitter.com/account/access")) {
	        	logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Activate Id Access");
				try {
					driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);	
			        // Actions 객체 생성
			        Actions actions = new Actions(driver);

			        // 마우스를 (x, y) 위치로 이동
			        ThreadUtil.sleepSec(3);
					String unLockInputXpath ="/html/body/div[2]/div/form/input[6]";
					WebElement unLockInput = driver.findElement(By.xpath(unLockInputXpath));
			        int x = unLockInput.getLocation().getX();
			        int y = unLockInput.getLocation().getY();
			        System.out.println("X : " + x );
			        System.out.println("y : " + y );
//			        actions.moveByOffset(x, y).perform();
			        actions.moveToElement(unLockInput).perform();
			        ThreadUtil.sleepSec(5);
			        actions.click().perform();
//			        actions.doubleClick(unLockInput).perform();
//			        actions.moveByOffset(x,y).click();
//					unLockInput.click();
					ThreadUtil.sleepSec(5);
					if(!driver.getCurrentUrl().equals("https://twitter.com/home?lang=en")) {
						logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Page is not https://twitter.com/home ");
						return null;
					} else if (driver.getCurrentUrl().equals("https://twitter.com/account/access?lang=en")) {
						String unLockInputXpath2 ="/html/body/div[2]/div/form/input[6]";
						WebElement unLockInput2 = driver.findElement(By.xpath(unLockInputXpath));
				        System.out.println("X : " + x );
				        System.out.println("y : " + y );
				        actions.moveToElement(unLockInput).perform();
				        ThreadUtil.sleepSec(5);
				        actions.click().perform();
				        ThreadUtil.sleepSec(5);
				        
				        if(!driver.getCurrentUrl().equals("https://twitter.com/home?lang=en")) {
							logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Page is not https://twitter.com/home ");
							return null;
				        }
					}
				} catch (ElementNotVisibleException e) {
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", CAPTCHA Login PASS or unLockInput Xpatgh Change");
					return null;
				}
	        } 
	        
			Cookie csrfName = null;
			Cookie cookiesName = null;
			String cookie = null;
			/* crsfToken 값과 cookie 값 생성후 Map 객체에 담기 */
			try {
				csrfName = driver.manage().getCookieNamed("ct0");
				cookiesName = driver.manage().getCookieNamed("auth_token");
				cookie = cookiesName.getName() + "=" + cookiesName.getValue() + ";" + csrfName.getName() + "=" + csrfName.getValue() + ";";
			}catch (NullPointerException e) { // 로그인 실패 또는 쿠키필드값 변경시 Exception
				e.printStackTrace();
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", crsfToken or cookie is null crsfToken: " + csrfName.getValue() +
						", cookie: " + cookiesName);
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check loginInfo id :" + id + 
						" ,password: " + password);
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check coikesName change");
				return null;
			}

			
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", csrfToken : " + csrfName.getValue());
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", cookie : " + cookie);
			loginInfo.put(MAP_TOKEN_KEY_NAME, csrfName.getValue());
			loginInfo.put(MAP_COOKIE_KEY_NAME, cookie);		
					
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			abort();
		}
		return loginInfo;
	}

}

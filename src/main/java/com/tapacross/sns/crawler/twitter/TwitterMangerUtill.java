package com.tapacross.sns.crawler.twitter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.Request;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.tapacross.sns.crawler.twitter.entity.TBTwitterXCTxidToken;
import com.tapacross.sns.crawler.twitter.service.TwitterLoginInfoService;
import com.tapacross.sns.entity.TBTwitterAuthToken;
import com.tapacross.sns.util.ThreadUtil;

public class TwitterMangerUtill {
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private final Logger logger = LoggerFactory.getLogger(TwitterMangerUtill.class);
	private final int MAX_RETRY = 5; // 최대 재시도 횟수
	private static final String MAP_TOKEN_KEY_NAME = "crsfToken";
	private static final String MAP_COOKIE_KEY_NAME = "cookie";
	private TwitterLoginInfoService service;
	private static int updateCount = 0;
	private static int updateFailCount = 0;
	private static List<String> updatefailList = new ArrayList<>();
	
	public void initSpringBeans() {
		@SuppressWarnings("resource")
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:spring/application-context.xml");		
		this.service = context.getBean(TwitterLoginInfoService.class);		
	}
	
	public ChromeDriver init (String proxyIp, int port, String profileDir) {
		ChromeDriver driver = null;
		try {
			ChromeOptions options = new ChromeOptions();
			
	        // 분리된 사용자 데이터 디렉토리 사용
	        options.addArguments("--user-data-dir=" + profileDir);
	        
		    options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
	        options.addArguments("--use-fake-ui-for-media-stream");
	        options.addArguments("--use-fake-device-for-media-stream");
//		    options.addArguments("--incognito"); // 크롬 씨크릿모드
	        options.addArguments("--lang=en");
//		    options.addArguments("--disable-cache");
//		    options.addArguments("--disable-cookies");
			options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
			options.addArguments("--no-sandbox"); // 샌드박스 비활성화
			options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
			options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
			options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
	        options.addArguments("Sec-Fetch-Site=same-origin");
	        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");	
	        options.addArguments("--remote-allow-origins=*"); // 4.12 phantomJs update후 사용
//		    Proxy proxy = new Proxy();
//		    proxy.setHttpProxy(proxyIp + ":" + port);
//		    proxy.setSslProxy(proxyIp + ":" + port);
//		    options.addArguments("--proxy-server=http://" + proxyIp + ":" + port);
	        
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
			String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
			String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
			
			try {
				driver = new ChromeDriver(options);
			} catch (Exception e) {
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver new ChromeDriver(options Error");
				e.printStackTrace();
				abort(driver, profileDir);
			}
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init OK");
		} catch (Exception e) {
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver init Exception to occur");
			e.printStackTrace();
		}
		return driver;
	}
	
	public void abort (ChromeDriver driver, String profileDir) {
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort Start");
		if (driver != null) {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NOT NULL");
			driver.close();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver CLOSE");
			driver.quit();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver QUIT");
			driver = null;
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
			deleteDirectory(new File(profileDir));
		} else {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
		}
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort End");
	}
	/*
	 * 아이디와 비밀번호를 받아 로그인한후 토큰값과 쿠키값을 반환한다.
	 * */
	public Map<String, String> selectTrsfAndCookie(ChromeDriver driver, String id, String password, String email, String proxyIP, int port){
		Map<String, String> loginInfo = new HashMap<>();
		try {
			String loginUrl = "https://twitter.com/i/flow/login";
			
				if(driver == null) {
					logger.info("driver is null, System exit");
					System.exit(1);
				}
							
			/* Connect Page */	
			boolean isConnected = false;
			int retryCount = 1;
			
			while(!isConnected) {			
				try {				
					if(retryCount > MAX_RETRY) {
						logger.error(TWITTER_LOGIN_INFO_MANAGER + ", Conenction Fail OR IdInputXpath fail Return null");
						return null;					
					}
					/* login page 이동*/
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver get login start");
					driver.get(loginUrl);
					ThreadUtil.sleepSec(5);
					driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver get login end");
					
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start, id : " + id); 
					
					// id입력으로 이동
					driverActions_Keys(driver,Keys.TAB,3);
					driverActions_data(driver, id);	
			        System.out.println("id perform");
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start end");
					isConnected = true;
				}
				catch (Exception e) {
						e.printStackTrace();
						logger.error(TWITTER_LOGIN_INFO_MANAGER + ", Twitter LoginPage Connection Error CheckIdXpath or Check URL : " + loginUrl);	
						logger.info(TWITTER_LOGIN_INFO_MANAGER + ", LoginPage RETRY... ");
						retryCount++;	
				}
			}
			try {	
				/* id 입력 후 클릭 */
				driverActions_Keys(driver,Keys.TAB,1);
				driverActions_Keys(driver,Keys.ENTER,1);
				ThreadUtil.sleepSec(3);
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idLoginInput Click");		
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				
				driverActions_data(driver, password);
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pwInput start end");
			}catch (NoSuchElementException e) {
				e.printStackTrace();
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check loginInfo id :" + id);
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", check pwXpath change");
				return null;
			}
			
			/* password 입력 후 클릭 */
			driverActions_Keys(driver,Keys.TAB,3);
			driverActions_Keys(driver,Keys.ENTER,1); 
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pwLoginInput Click");		
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			/*메일인증 페이지*/
			ThreadUtil.sleepSec(3);// 테스트

			if(driver.getCurrentUrl().equals("https://x.com/i/flow/login?mx=2")) {
				try {
				String pwTextXpath ="/html/body/div[1]/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div/div/span/span";
				String pwText = driver.findElement(By.xpath(pwTextXpath)).getText();
				
				System.out.println("pwText : " + pwText);
					
					if(pwText.contains("email address associated with your X account. ")) { // 간단한 이메일 인증
						logger.error(TWITTER_LOGIN_INFO_MANAGER + ", email address associated with your X account.");
						driverActions_data(driver, email);
						driverActions_Keys(driver, Keys.TAB, 3);
						driverActions_Keys(driver, Keys.ENTER, 1);
						ThreadUtil.sleepSec(3);	
					} else if (pwText.contains("Enter it below to sign in.")) { // 일정시간 지나면 이메일 클릭으로 바뀌기도함, 추후변경  Enter it below to sign in./ test를 위해 주석처리
						logger.error(TWITTER_LOGIN_INFO_MANAGER + ", Email authentication Pass, email:" + email);	
						service.updateAuthTokenStatusE(email);
						return null;
					}		
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", email Activate Error :" + email);
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", OR password error check pwd :" + password);
					return null;
				}

			}
						
	        
	        if(driver.getCurrentUrl().equals("https://twitter.com/account/access")) {
	        	logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Activate Id Access");
				try {
		        	service.updateAuthTokenStatusC(email);
					ThreadUtil.sleepSec(3);
					/* home화면 나오지 않을시 실패로 판단 */
					if(!driver.getCurrentUrl().contains("https://twitter.com/home")) {
						service.updateAuthTokenStatusC(email);
						logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Activate Id Access fail, CAPTHCA Check!!");
						return null;
					} 
					
				} catch (ElementNotInteractableException e) {
					service.updateAuthTokenStatusC(email);
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
		} 
		return loginInfo;
	}
	
	public List<TBTwitterAuthToken> returnUpdateList() {
		List<TBTwitterAuthToken> statusFList = new ArrayList<TBTwitterAuthToken>();
		statusFList = service.selectStatusFTwitterIdList();
		return statusFList;
	}
	
	/*
	 * 트위터 block된 id를 수동으로 해제후 활성화 시킨다.
	 * 현재 block 계정 업데이트하는 STATUS 값 : X
	 *  
	 * */
	public void updateTwitterAuthData(ChromeDriver driver,TBTwitterAuthToken dbAtuh, Map<String,String> loginInfo) {	
		try {					
				if(loginInfo == null) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", loginInfo is null Pass");
            		updateFailCount ++;
            		updatefailList.add(dbAtuh.userId);
            		return;
				}else {
					TBTwitterAuthToken entity = new TBTwitterAuthToken();
					entity.email = dbAtuh.email;
					entity.userId = dbAtuh.userId;

					entity.userPassword = dbAtuh.userPassword;
					entity.token =loginInfo.get(MAP_TOKEN_KEY_NAME);
					entity.cookie =loginInfo.get(MAP_COOKIE_KEY_NAME);
					
					try {
						service.updateTwitterAuthData(entity);
						updateCount ++;
					}catch (CannotGetJdbcConnectionException e) {
						e.printStackTrace();
					}
				}
		}catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	
	public void parseAndInsertXCTXIDToken(ChromeDriver driver, String ip, int targetCount, String profileDir) {
        DevTools devTools = driver.getDevTools();
		try {
			int curentCount = 0;
			logger.info("parseAndInsertXCTXIDToken start");
	        driver.get("https://x.com/search?q=%23bts&src=trend_click&f=live&vertical=trends");
			ThreadUtil.sleepSec(10);
			
	        devTools.createSession();
//	        // 네트워크 활동 모니터링 설정
	        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
	        devTools.addListener(Network.requestWillBeSent(), request -> {
	            Request req = request.getRequest();
	//	        System.out.println("URL: " + req.getUrl());
	//	        System.out.println("Method: " + req.getMethod());
	//	        System.out.println("Headers: " + req.getHeaders());	  
	            // 유효하지않은 값일때 바로 종료 로직 필요
	            
	            if(req.getUrl().contains("SearchTimeline?variables=")) {
	            	logger.info("req.getUrl().contains(SearchTimeline?variables=)");
	            	String xcTxid = req.getHeaders().get("x-client-transaction-id").toString();
	            	if(!xcTxid.isEmpty()) {
	            		logger.info("xcTxid= " + xcTxid + ", insert start");
		            	// 서비스로직
	            		TBTwitterXCTxidToken entity = new TBTwitterXCTxidToken();
	            		entity.setToken(xcTxid);
	            		entity.setIp(ip);
	            		service.insertXCTxidToken(entity);
	            		logger.info("xcTxid insert, xcTxid = " + xcTxid + ", ip: " + ip);
	            		
	            	}
	            }
	        });	 
	        
			while(curentCount < targetCount) {
	            ThreadUtil.sleepSec(10);
		        driver.navigate().refresh();
	            curentCount ++;
	            logger.info("parseAndInsertXCTXIDToken count: " + curentCount);
			}
			devTools.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			devTools.close();
			abort(driver, profileDir);
		}
	}
		
	public void driverActions_data(WebDriver driver, String data) {
		Actions actions = new Actions(driver);
		actions.sendKeys(data).perform();
		ThreadUtil.sleep(500);
	}
	
	public void driverActions_Keys(WebDriver driver, Keys key, int count) {
		Actions actions = new Actions(driver);
		
		int num = 0;
		while(count > num) {
			actions.sendKeys(key).perform();
			System.out.println(key.name());
			ThreadUtil.sleep(500);
			num ++;
		}			
	}
	
	public String createProfileDir() {
		return "C:/temp/chrome-profile-" + UUID.randomUUID();
	}
	
	public void deleteDirectory(File dir) {
	    if (dir == null || !dir.exists()) {
	        logger.warn("Directory does not exist: " + dir);
	        return;
	    }

	    if (dir.isDirectory()) {
	        File[] files = dir.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                deleteDirectory(file);
	            }
	        }
	    }

	    boolean deleted = dir.delete();
	    if (deleted) {
	        logger.info("Deleted: " + dir.getAbsolutePath());
	    } else {
	        logger.warn("Failed to delete: " + dir.getAbsolutePath());
	    }
	}
	
	public List<TBTwitterAuthToken> selectTwitterAuthList(String status) {
		return service.selectAuthTokenStatus(status);
	}
	
	public List<TBTwitterAuthToken> selectTwitterAuthList(String ip, String status) {
		return service.selectAuthTokenIpAndStatus(ip, status);
	}
}

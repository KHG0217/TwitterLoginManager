package com.tapacross.sns.crawler.twitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
import com.tapacross.sns.util.FileUtil;
import com.tapacross.sns.util.ThreadUtil;


public class TwitterLoginInfoManager {
	private WebDriver driver;
	private WebDriver ruuKrDriver;
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private static final String MAP_TOKEN_KEY_NAME = "crsfToken";
	private static final String MAP_COOKIE_KEY_NAME = "cookie";
	private final Logger logger = LoggerFactory.getLogger(TwitterLoginInfoManager.class);
	private TwitterLoginInfoService service;
	private static int insertCount = 0;
	private static int insertFailCount = 0;
	private static int updateCount = 0;
	private static int updateFailCount = 0;
	private static List<String> proxyIpList = new ArrayList<>();
	private static List<String> insertfailList = new ArrayList<>();
	private static List<String> updatefailList = new ArrayList<>();
	private static int ProxyIpListindex = 0;
	private static int ProxyIpListSize = 0;
	
	 private final int MAX_RETRY = 5; // 최대 재시도 횟수 https://twitter.com/account/access?lang=en
	
	 /*
	  * type = 1 : insert
	  * type = 2 : update
	  * */
	public static void main(String[] args) {		
		TwitterLoginInfoManager manger = new TwitterLoginInfoManager();
		String proxyPath ="target/classes/data/haiip.txt";			
		try {

			System.out.println("type select,  1: insert, 2: update, :");
			Scanner scanner = new Scanner(System.in);	
			int number = scanner.nextInt(); // 정수를 읽음
			scanner.close();
			
			String lines = FileUtil.readFromFile(proxyPath, "\r\n");
			String[] splitStrings = lines.split("\r\n");
			
			for (String splitString : splitStrings ) {
				proxyIpList.add(splitString);
			}
						
			ProxyIpListSize = proxyIpList.size() - 1;		
			Random random = new Random();
			ProxyIpListindex = random.nextInt(proxyIpList.size() -1);
			Map.Entry<String, Integer> proxyIpInfo = manger.distinguishIpAndProt(ProxyIpListindex);
			String proxyIp = proxyIpInfo.getKey();
			int port = proxyIpInfo.getValue();
				
			String date = manger.currentDate();
			switch (number) {
			case 1:   
				System.out.println("----------------INSERT START---------------------------------");
				System.out.println("----------------CURRENT DATE: " + date + "-------------");		
				manger.insertNewTwitterId(proxyIp,port);
				System.out.println("----------------INSERT END---------------------------------");
				System.out.println("insertCount : " + insertCount);
				System.out.println("insertFailCount : " + insertFailCount);
				System.out.println("insertFail list : ");
				for(String failId : insertfailList) {
					System.out.println(failId);
				}
				break;
				
			case 2:
				System.out.println("----------------UPDATE START---------------------------------");
				System.out.println("----------------CURRENT DATE: " + date + "-------------");				
				manger.updateTwitterAuthData(proxyIp, port); // 상태값 C값으로 찾아서 업데이트 - test중
				System.out.println("----------------UPDATE END---------------------------------");
				System.out.println("updateCount : " + updateCount);
				System.out.println("updateFailCount : " + updateFailCount);
				for(String failId : updatefailList) {
					System.out.println(failId);
				}
				break;
				
			default:
				System.out.println("arg is 1 or 2...or SYSTEM EXIT...");
				System.exit(1);
				break;
			}


			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public String currentDate() {
		String date = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        date = formattedTime;       
        return date;
	}
	
	public Map.Entry<String, Integer> distinguishIpAndProt(int proxyIpListIndex) {
		String[] ipAddress = proxyIpList.get(proxyIpListIndex).split(":");
		String proxyIp = ipAddress[0];
		int port = Integer.parseInt(ipAddress[1]);		
		
		Random random = new Random();
		ProxyIpListindex = random.nextInt(proxyIpList.size() -1);
		return new AbstractMap.SimpleEntry<>(proxyIp, port);
	}
	
	private void initSpringBeans() {
		@SuppressWarnings("resource")
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:spring/application-context.xml");		
		this.service = context.getBean(TwitterLoginInfoService.class);		
	}

	/*현재 proxy 사용 x */
	private void init (String proxyIp, int port) {
		try {
			this.driver = null;
			if(this.driver == null) {
				ChromeOptions options = new ChromeOptions();
		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
//		        options.addArguments("--use-fake-ui-for-media-stream");
//		        options.addArguments("--use-fake-device-for-media-stream");
		        options.addArguments("--incognito"); // 크롬 씨크릿모드
//		        options.addArguments("--lang=en");
//		        options.addArguments("--disable-cache");
//		        options.addArguments("--disable-cookies");
				options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
				options.addArguments("--no-sandbox"); // 샌드박스 비활성화
				options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
				options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
				options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
//		        options.addArguments("--remote-debugging-address=127.0.0.1"); // -> 문제발생 (23.07.10) 
//		        options.addArguments("--remote-debugging-port=9222"); // -> 문제발생 (23.07.10)
		        options.addArguments("Sec-Fetch-Site=same-origin");
		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
		        										        
		        Proxy proxy = new Proxy();
		        proxy.setHttpProxy(proxyIp + ":" + port);
		        proxy.setSslProxy(proxyIp + ":" + port);
//		        options.addArguments("--proxy-server=http://" + proxyIp + ":" + port);
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.driver = new ChromeDriver(options);
//					this.driver = new ChromeDriver();
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
	
	private void initruuKr() {
		try {
			this.ruuKrDriver = null;
			if(this.ruuKrDriver == null) {
				ChromeOptions options = new ChromeOptions();
//		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
//		        options.addArguments("--use-fake-ui-for-media-stream");
//		        options.addArguments("--use-fake-device-for-media-stream");
		        options.addArguments("--incognito"); // 크롬 씨크릿모드
		        options.addArguments("--lang=en");
//		        options.addArguments("--disable-cache");
//		        options.addArguments("--disable-cookies");
				options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
				options.addArguments("--no-sandbox"); // 샌드박스 비활성화
				options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
				options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
				options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
//		        options.addArguments("--remote-debugging-address=127.0.0.1"); // -> 문제발생 (23.07.10) 
//		        options.addArguments("--remote-debugging-port=9222"); // -> 문제발생 (23.07.10)
		        options.addArguments("Sec-Fetch-Site=same-origin");
		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
		        										        
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", ruuKrDriver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.ruuKrDriver = new ChromeDriver(options);
//					this.driver = new ChromeDriver();
				} catch (Exception e) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", ruuKrDriver new ChromeDriver(options Error");
					e.printStackTrace();
					ruuDriverAbort();
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
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort Start");
		if (driver != null) {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NOT NULL");
			driver.close();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver CLOSE");
			driver.quit();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver QUIT");
			driver = null;
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
		} else {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
		}
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort End");
	}
	
	private void ruuDriverAbort () {
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort Start");
		if (driver != null) {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NOT NULL");
			ruuKrDriver.close();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver CLOSE");
			ruuKrDriver.quit();
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver QUIT");
			ruuKrDriver = null;
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
		} else {
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver NULL");
		}
		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", abort End");
	}
	
	/**
	 *  Lock log 찾기 insert fail Lock email
	 */
	public void insertNewTwitterId(String proxyIp, int port) {
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
            	
            	Map<String,String> loginInfo = selectTrsfAndCookie(id, password, email, proxyIp, port);
            	
            	if(loginInfo == null) {
            		logger.error(TWITTER_LOGIN_INFO_MANAGER + ", loginInfo is null Pass");
            		logger.error(TWITTER_LOGIN_INFO_MANAGER + ", insert fail Lock email, id: " + id);
            		insertFailCount ++;
//					Map.Entry<String, Integer> proxyIpInfo = distinguishIpAndProt(ProxyIpListindex);
//					proxyIp = proxyIpInfo.getKey();
//					port = proxyIpInfo.getValue();
					
//					System.out.println("Change proxyIp : " + proxyIp);
//					System.out.println("Change port : " + port);
					insertfailList.add(id);
            		continue;
            	}        	
        		entity.email = email;
        		entity.userId = id;
        		entity.userPassword =password;
        		entity.token =loginInfo.get(MAP_TOKEN_KEY_NAME);
        		entity.cookie =loginInfo.get(MAP_COOKIE_KEY_NAME); 
        		
        		try {
            		service.insertTwitterNewId(entity);  
            		insertCount ++;
        		}catch (CannotGetJdbcConnectionException e) {
					e.printStackTrace();
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", insert fail DB Error, email: " + email);
					insertFailCount ++;
				}
 	  
            } 
            reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void updateTwitterAuthData(String proxyIp, int port) {
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
				String email = data.email;
				System.out.println("proxyIp : " + proxyIp);
				System.out.println("port : " + port);
				Map<String,String> loginInfo = selectTrsfAndCookie(id,password,email,proxyIp,port);
				
				if(loginInfo == null) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", loginInfo is null Pass");
            		logger.error(TWITTER_LOGIN_INFO_MANAGER + ", update fail Lock email, id: " + id);
            		updateFailCount ++;
            		updatefailList.add(id);
//					Map.Entry<String, Integer> proxyIpInfo = distinguishIpAndProt(ProxyIpListindex);
//					proxyIp = proxyIpInfo.getKey();
//					port = proxyIpInfo.getValue();
					
//					System.out.println("Change proxyIp : " + proxyIp);
//					System.out.println("Change port : " + port);
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
						updateCount ++;
					}catch (CannotGetJdbcConnectionException e) {
						e.printStackTrace();
					}
				}
				

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	public Map<String, String> selectTrsfAndCookie(String id, String password, String email, String proxyIP, int port){
		Map<String, String> loginInfo = new HashMap<>();
		try {
			String loginUrl = "https://twitter.com/i/flow/login";
			
				if(driver == null) {
					init(proxyIP,port);
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
					driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver get login end");
					
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start, id : " + id);
					String idXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[5]/label/div/div[2]/div/input";
					WebElement idInput = driver.findElement(By.xpath(idXpath)); // 프록시 사용으로 연결 실패시, 해당 엘리멘트를 찾지못하는 경우 발생 
					idInput.sendKeys(id);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idInput start end");
					isConnected = true;
//					ThreadUtil.sleepSec(3);
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
				String idLoginInputXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[6]";
				WebElement idLoginInput = driver.findElement(By.xpath(idLoginInputXpath));
				idLoginInput.click();
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", idLoginInput Click");		
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//				ThreadUtil.sleepSec(3);
				
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

			/* email 추가인증 요구시 */
			if(driver.getCurrentUrl().equals("https://twitter.com/i/flow/login")) {
				
				try {
				/* 비밀번호 오류 체크로직 */
//				String pwTextXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/h1/span/span";
				String pwTextXpath ="/html/body/div[1]/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div/div/span";
				String pwText = driver.findElement(By.xpath(pwTextXpath)).getText();
				
				System.out.println("pwText : " + pwText);
					
				if(pwText.equals("Enter your password")) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", Password Error Check id and password,  id:" + id +" , password : " + password);
					return null;
				} else if (pwText.contains("Enter it below to sign in.")) { // 일정시간 지나면 이메일 클릭으로 바뀌기도함
//					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", Email authentication Pass, email:" + email);
//					initruuKr();
//					String ruukrUrl = "http://ruu.kr/";
////					
//					ruuKrDriver.get(ruukrUrl);
//					String ruuKrinputXpath ="/html/body/div[1]/div[1]/div/div[1]/form/input[3]";
//					WebElement ruuKrinput = driver.findElement(By.xpath(ruuKrinputXpath));
//					String email1 = email.replaceAll("@.+", "");
//					System.out.println("email1 : " + email1);
//					ruuKrinput.sendKeys(email1);
//					
//					ThreadUtil.sleepSec(1000);
					return null;
					
				}
				

					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", email input start, email : " + email);
					String emailXpath ="/html/body/div[1]/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/label/div/div[2]/div/input";
					WebElement emailInput = driver.findElement(By.xpath(emailXpath));
					emailInput.sendKeys(email);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", email input end");
					
					String emailLoginXpath ="/html/body/div[1]/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div";
					WebElement emailLoginInput = driver.findElement(By.xpath(emailLoginXpath));
					emailLoginInput.click();
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", emailInput Click");		
					driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
					ThreadUtil.sleepSec(3);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", email Activate Error :" + email);
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", OR password error check pwd :" + password);
					return null;
				}

			}
						
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
			        ThreadUtil.sleepSec(7);
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
			        
			        if(x == 0 && y == 0) {
			        	System.out.println("캡챠라고 임시 판단 null 처리");
			        	return null;
			        }
					
					if (driver.getCurrentUrl().equals("https://twitter.com/account/access?lang=en")) { // start Click -> Continue to Twitter
						String unLockInputXpath2 ="/html/body/div[2]/div/form/input[6]";
						WebElement unLockInput2 = driver.findElement(By.xpath(unLockInputXpath2));
				        int x2 = unLockInput2.getLocation().getX();
				        int y2 = unLockInput2.getLocation().getY();
				        System.out.println("x2: " + x2 );
				        System.out.println("y2 : " + y2 );
				        actions.moveToElement(unLockInput2).perform();
				        ThreadUtil.sleepSec(5);
				        actions.click().perform();
				        ThreadUtil.sleepSec(5);
				        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Activate Id Access Continue Botton Click");
				        ThreadUtil.sleepSec(5);
				        				        
					}
					
			        
					ThreadUtil.sleepSec(7);
					/* home화면 나오지 않을시 실패로 판단 */
					if(!driver.getCurrentUrl().contains("https://twitter.com/home?")) {
						logger.info(TWITTER_LOGIN_INFO_MANAGER + ", Activate Id Access fail, CAPTHCA Check!!");
						return null;
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
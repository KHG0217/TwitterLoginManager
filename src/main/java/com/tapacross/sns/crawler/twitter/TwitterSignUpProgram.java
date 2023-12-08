package com.tapacross.sns.crawler.twitter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapacross.sns.util.FileUtil;
import com.tapacross.sns.util.ThreadUtil;

public class TwitterSignUpProgram {
	private WebDriver driverTwitter;
	private WebDriver driverRuuKr;
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private final Logger logger = LoggerFactory.getLogger(TwitterLoginInfoManager.class);
	private static List<String> proxyIpList = new ArrayList<>();
	
	private void initRuuKr () {
		try {
			this.driverRuuKr = null;
			if(this.driverRuuKr == null) {
				ChromeOptions options = new ChromeOptions();
//		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
//		        options.addArguments("--incognito");
//		        options.addArguments("--disable-cache");
//		        options.addArguments("--disable-cookies");
//				options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
//				options.addArguments("--no-sandbox"); // 샌드박스 비활성화
//				options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
//				options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
//				options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
//				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
//		        options.addArguments("--remote-debugging-address=127.0.0.1"); -> 문제발생 (23.07.10) 
//		        options.addArguments("--remote-debugging-port=9222"); -> 문제발생 (23.07.10)
//		        options.addArguments("Sec-Fetch-Site=same-origin");
//		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
		        
										     
//		        String proxyHost = "121.126.129.148";
//		        int proxyPort = 5252;
//		        Proxy proxy = new Proxy();
//		        proxy.setHttpProxy(proxyHost + ":" + proxyPort);
//		        proxy.setSslProxy(proxyHost + ":" + proxyPort);
//		        options.addArguments("--proxy-server=http://" + proxyHost + ":" + proxyPort);
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.driverRuuKr = new ChromeDriver(options);
//					this.driver = new ChromeDriver();
				} catch (Exception e) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver new ChromeDriver(options Error");
					e.printStackTrace();
					abortTwitterDriver();
				}
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init OK");
//				ThreadUtil.sleepSec(1000);
			}
		} catch (Exception e) {
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver init Exception to occur");
			e.printStackTrace();
			}
	}
	
	private void initTwitterDriver (String proxyIp, int port) {
		try {
			this.driverTwitter = null;
			if(this.driverTwitter == null) {
				ChromeOptions options = new ChromeOptions();
//		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
				options.addArguments("--remote-debugging-port=9222");
		        options.addArguments("--use-fake-ui-for-media-stream");
		        options.addArguments("--use-fake-device-for-media-stream");
		        options.addArguments("--incognito"); // 크롬 씨크릿모드
		        options.addArguments("--lang=kr");
		        options.addArguments("--disable-cache");
		        options.addArguments("--disable-cookies");
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
		        options.addArguments("--proxy-server=http://" + "115.144.130.41" + ":" + 5042);
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.driverTwitter = new ChromeDriver(options);
//					this.driver = new ChromeDriver();
				} catch (Exception e) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver new ChromeDriver(options Error");
					e.printStackTrace();
					abortTwitterDriver();
				}
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init OK");
//				ThreadUtil.sleepSec(1000);
			}
		} catch (Exception e) {
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver init Exception to occur");
			e.printStackTrace();
			}
	}
	
	private void abortRuuKrDriver () {
		if (driverRuuKr != null) {
			driverRuuKr.close();
			driverRuuKr.quit();
			driverRuuKr = null;
		}
	}
	
	private void abortTwitterDriver () {
		if (driverTwitter != null) {
			driverTwitter.close();
			driverTwitter.quit();
			driverTwitter = null;
		}
	}
	
	public String AuthenticateEmail(String email) {	
		try {
			initRuuKr();
		} catch (Exception e) {
			e.printStackTrace();
			abortRuuKrDriver();
		}

		String certificationNumber = null;
		
		driverRuuKr.get("http://ruu.kr/");
		ThreadUtil.sleepSec(3);
		
		String inputXpath ="/html/body/div[1]/div[1]/div/div[1]/form/input[3]";
		WebElement emailInput = driverRuuKr.findElement(By.xpath(inputXpath));
		emailInput.sendKeys(email);
		
		ThreadUtil.sleepSec(60);
		
		String emailXpath = "/html/body/div[1]/div[1]/div/div[3]";
		WebElement emailEl = driverRuuKr.findElement(By.xpath(emailXpath));
		
		
		
		certificationNumber = emailEl.getText()
				.replaceAll("입니다.+", "")
				.replaceAll("[^0-9]", "");		
				
//		certificationNumber ="낸이 제목 시간 삭제\r\ninfo@twitter.com 트위터 인증 코드는 321511입니다 2023-07-11 11:32:06.0 삭제"	
//		.replaceAll("\r\n", "")
//		.replaceAll("입니다.+", "")
//		.replaceAll("[^0-9]", "");
		System.out.println(certificationNumber);
				
		
		System.out.println("certificationNumber : " + certificationNumber);
		
		
		
		return certificationNumber;
	}
	
	public void SignUpTwitter(String email, String name, String proxyIp , int port) {
		try {
			initTwitterDriver(proxyIp, port);
		} catch (Exception e) {
			e.printStackTrace();
			abortTwitterDriver();
		}

		try {
			driverTwitter.get("https://twitter.com/i/flow/login");
	        // WebDriverWait 인스턴스 생성
	        WebDriverWait urlWait = new WebDriverWait(driverTwitter, 10);
	        
	        ThreadUtil.sleepSec(3);
			
	        /* 가입하기 클릭 */
			String signUpInputXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[8]/span[2]";
			WebElement signUpInput = driverTwitter.findElement(By.xpath(signUpInputXpath));
	        Actions actions = new Actions(driverTwitter);
	        actions.moveToElement(signUpInput).perform();
	        ThreadUtil.sleepSec(3);
	        actions.click().perform();    
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", signUpInput Click" );
	        ThreadUtil.sleepSec(3);
	        
	        String targetURL ="https://twitter.com/i/flow/signup";
	        urlWait.until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver driver) {
	                return driver.getCurrentUrl().equals(targetURL);
	            }
	        });
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", URL Loding OK URL: + "+ targetURL );
	        
	        /* 가입하기 클릭 */
			String signUpInputXpath2 ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[5]";
			WebElement signUpInput2 = driverTwitter.findElement(By.xpath(signUpInputXpath2));
	        actions.moveToElement(signUpInput2).perform();
	        ThreadUtil.sleepSec(3);
	        actions.click().perform();
	                
	        // 이메일 사용하기 클릭
	        String changeEmailUseXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[3]";
	        WebElement changeEmailUse = driverTwitter.findElement(By.xpath(changeEmailUseXpath));
	        actions.moveToElement(changeEmailUse).perform();
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", changeEmailUse Click" );
	        actions.click().perform();
	        ThreadUtil.sleepSec(3);
	        
	        /* click 누른 후 아이디 입력창으로 이동*/
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	                
	        actions.sendKeys(email).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        
	        actions.sendKeys(email).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        
	        Random random = new Random();
	        
	        String randomD = Integer.toString(random.nextInt(20)+1);
	        actions.sendKeys(randomD).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        String randomM = Integer.toString(random.nextInt(11)+1);
	        actions.sendKeys(randomM).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        String randomY = Integer.toString(random.nextInt(25)+1960);
	        actions.sendKeys(randomY).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(2);

	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        /* 다음버튼 클릭*/
//			String nextInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div";
//			WebElement nextInput = driverTwitter.findElement(By.xpath(nextInputXpath));
//	        actions.moveToElement(nextInput).perform();
//	        ThreadUtil.sleepSec(1);
//	        actions.click().perform();
	        
	        /* 가입 클릭*/
//			String signUpComplateInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div";
//			WebElement signUpComplateInput = driverTwitter.findElement(By.xpath(signUpComplateInputXpath));
//	        actions.moveToElement(signUpComplateInput).perform();
//	        ThreadUtil.sleepSec(1);
//	        actions.click().perform();
	        	        
	        actions.click().perform();
	        actions.sendKeys(Keys.TAB).perform();
	        ThreadUtil.sleepSec(3);
	        actions.sendKeys(Keys.TAB).perform();
	        
	        String code = AuthenticateEmail(name);
	        
	        if(code.isEmpty()) {
	        	logger.error("certificationNumber is NULL change Proxy IP and host OR ruu.kr Site Status Check");
	        	System.exit(1);
	        }
	        ThreadUtil.sleepSec(1);
	        actions.sendKeys(code).perform();
	        abortRuuKrDriver();
	        
	        /* 다음버튼 클릭 2 */
			String nextInput2Xpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div";
			WebElement nextInput2 = driverTwitter.findElement(By.xpath(nextInput2Xpath));
	        actions.moveToElement(nextInput2).perform();
	        ThreadUtil.sleepSec(1);
	        actions.click().perform();
	        
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys("tapaman1234").perform();
	        
	        /* 다음버튼 클릭 3 */
			String nextInput3Xpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div";
			WebElement nextInput3 = driverTwitter.findElement(By.xpath(nextInput3Xpath));
	        actions.moveToElement(nextInput3).perform();
	        ThreadUtil.sleepSec(1);
	        actions.click().perform();	        
	        ThreadUtil.sleepSec(5);
	        
		       /* 프로필 설정 화면 간헐적 등장*/
	        if(driverTwitter.getCurrentUrl().equals("https://twitter.com/i/flow/signup")) {
	        	actions.keyDown(Keys.CONTROL).sendKeys(Keys.F5).keyUp(Keys.CONTROL).perform(); //새로고침
	        	ThreadUtil.sleepSec(5);
	        	
		        String userNameXpath = "/html/body/div[1]/div/div/div[2]/header/div/div/div/div[2]/div/div/div[2]/div/div[2]/div/div/div/span";
		        WebElement userNameEl = driverTwitter.findElement(By.xpath(userNameXpath));
		        String userName = userNameEl.getText()
		        		.replaceAll("\\@", "");
		        
	            System.out.println("email : " + email);
	            System.out.println("userId : " + userName);
	            System.out.println("password : tapaman1234");
	            System.out.println("SignUp OK");
	                        
//	            abortTwitterDriver();
	        }
	        
	        
	        
	        /* Something wnet wrong error 발생 */
	        if(driverTwitter.getCurrentUrl().equals("https://twitter.com/account/access?flow=signup")) {
	        	actions.keyDown(Keys.CONTROL).sendKeys(Keys.F5).keyUp(Keys.CONTROL).perform(); //새로고침
	        	ThreadUtil.sleepSec(5);
	        }
	        
	        /* userName 확인 */
	        String userNameXpath = "/html/body/div[2]/div/div[2]/div/span";
	        WebElement userNameEl = driverTwitter.findElement(By.xpath(userNameXpath));
	        String userName = userNameEl.getText()
	        		.replaceAll("\\@", "");
	               
            System.out.println("email : " + email);
            System.out.println("userId : " + userName);
            System.out.println("password : tapaman1234");
            System.out.println("SignUp OK");
            
//            System.out.println("logout process..");
//            
//            String optionXpath ="/html/body/div[1]/div/ul/li";
//            Actions actions2 = new Actions(driverTwitter);
//			WebElement option = driverTwitter.findElement(By.xpath(optionXpath));
//	        int x = option.getLocation().getX();
//	        int y = option.getLocation().getY();
//	        System.out.println("X : " + x );
//	        System.out.println("y : " + y );
//	        actions2.moveByOffset(x, y).perform();
//	        ThreadUtil.sleepSec(3);
//            
//			String logOutButtonXpath ="/html/body/div[1]/div/ul/li/div/div/ul/li[2]";
//			WebElement logOutButton = driverTwitter.findElement(By.xpath(logOutButtonXpath));
//			actions2.moveToElement(logOutButton).build().perform();
//			System.out.println("logOutButton moveToElement");
//	        ThreadUtil.sleepSec(1);
//	        actions2.click().perform();
//			System.out.println("logOutButton click");
//	        
//	        ThreadUtil.sleepSec(3);
//	        reSignUp();
        
//	        /* 계정 활성화 - Start버튼 클릭 */      
//			String startInputXpath ="/html/body/div[2]/div/form/input[6]";
//			WebElement startInput = driverTwitter.findElement(By.xpath(startInputXpath));
//	        actions.moveToElement(startInput).perform();
//	        ThreadUtil.sleepSec(1);
//	        actions.click().perform();
//	        ThreadUtil.sleepSec(5);
//	        
//	        if(driverTwitter.getCurrentUrl().equals("https://twitter.com/account/access?lang=en&flow=signup")) {
//	            /* 계정 활성화 - Continie 버튼 클릭 */      
//	    		String continueInputXpath ="/html/body/div[2]/div/form/input[6]";
//	    		WebElement continueInput = driverTwitter.findElement(By.xpath(continueInputXpath));
//	            actions.moveToElement(continueInput).perform();
//	            ThreadUtil.sleepSec(1);
//	            actions.click().perform();
//	            ThreadUtil.sleepSec(1);
//	            
//	            System.out.println("email : " + email);
//	            System.out.println("userId : " + userName);
//	            System.out.println("password : tapaman1234");
//	            System.out.println("SignUp OK");
//	            abortTwitterDriver();
//	        } else {
//	        	ThreadUtil.sleepSec(1000);
//	            System.out.println("email : " + email);
//	            System.out.println("userId : " + userName);
//	            System.out.println("password : tapaman1234");
//	            System.out.println("SignUp fail");
//	        }
	        
	        
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortRuuKrDriver();
			abortTwitterDriver();
		}

        
        // 내일 테스트 
        
        
        
       
        
//        actions.sendKeys(Keys.ENTER).perform();
        
//        // 이름 입력     
//		String inputNameInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[1]/label/div/div[1]";
//		WebElement inputNameInput = driverRuuKr.findElement(By.xpath(inputNameInputXpath));
//		inputNameInput.click();
//		ThreadUtil.sleepSec(1);
//		logger.info(TWITTER_LOGIN_INFO_MANAGER + ", inputNameInput Click" );       
        
//        By.xpath("//*[contains(@class, 'element-class')]")
		
//		String inputNameInputXpath2 ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[1]/label/div/div[2]/div/input";
//		WebElement inputNameInput2 = driverRuuKr.findElement(By.xpath(inputNameInputXpath2));
		
//		inputNameInput2.sendKeys(name);      
//        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", inputNameInput, name : " + name );
        
//        // 이메일 입력
//		String inputEmailInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[1]";
//		WebElement inputEmailInput = driverRuuKr.findElement(By.xpath(inputEmailInputXpath));
//		inputEmailInput.sendKeys(email);      
//        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", inputEmailInput, email : " + email );
//        
//        // select box 월 입력
//        String SelectBoxMXpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[4]/div[3]/div/div[1]/select";
//        WebElement SelectBoxM = driverRuuKr.findElement(By.xpath(SelectBoxMXpath));
//        SelectBoxM.sendKeys("12");
//        
//        // select box 일 입력
//        String SelectBoxDXpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[4]/div[3]/div/div[2]/select";
//        WebElement SelectBoxD = driverRuuKr.findElement(By.xpath(SelectBoxDXpath));
//        SelectBoxD.sendKeys("29");
//        
//        // select box 년 입력
//        String SelectBoxYXpath = "/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[4]/div[3]/div/div[3]/select";
//        WebElement SelectBoxY = driverRuuKr.findElement(By.xpath(SelectBoxYXpath));
//        SelectBoxY.sendKeys("1996");
         	
	}
	
	public static void main(String[] args) {
		TwitterSignUpProgram signUpPro = new TwitterSignUpProgram();
//		signUpPro.AuthenticateEmail("tapa2307119603");
		String proxyPath ="target/classes/data/haiip.txt";	
		
		try {
			String lines = FileUtil.readFromFile(proxyPath, "\r\n");
			String[] splitStrings = lines.split("\r\n");
			
			for (String splitString : splitStrings ) {
				proxyIpList.add(splitString);
			}
			int proxyIpListSize = proxyIpList.size() - 1;
			int count = 0 ;
			for (int i = 21; 60 > i; i++) {
				String email = null;
				String id = null;
				String emailPrex = "tapa23112296";
				Map.Entry<String, Integer> proxyIpInfo = distinguishIpAndProt(i);
				String proxyIp = proxyIpInfo.getKey();
				int port = proxyIpInfo.getValue();
				if(i < 10) {
					email = emailPrex + "0" + Integer.toString(i) + "@ruu.kr";
					id = emailPrex + "0" + Integer.toString(i);
				}else {
					email = emailPrex + Integer.toString(i) + "@ruu.kr";
					id = emailPrex + Integer.toString(i);	
				}

				System.out.println(proxyIp + ":" + port);
				signUpPro.SignUpTwitter(email,id,proxyIp,port);
				count++;
				System.out.println("count : " + count);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void reSignUp() {
		try {
			driverTwitter.get("https://twitter.com/i/flow/login");
	        // WebDriverWait 인스턴스 생성
	        WebDriverWait urlWait = new WebDriverWait(driverTwitter, 10);
	        
	        ThreadUtil.sleepSec(3);
			
	        /* 가입하기 클릭 */
			String signUpInputXpath ="/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[8]/span[2]";
			WebElement signUpInput = driverTwitter.findElement(By.xpath(signUpInputXpath));
	        Actions actions = new Actions(driverTwitter);
	        actions.moveToElement(signUpInput).perform();
	        ThreadUtil.sleepSec(3);
	        actions.click().perform();    
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", signUpInput Click" );
	        ThreadUtil.sleepSec(3);
	        
	        String targetURL ="https://twitter.com/i/flow/signup";
	        urlWait.until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver driver) {
	                return driver.getCurrentUrl().equals(targetURL);
	            }
	        });
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", URL Loding OK URL: + "+ targetURL );
	        
	        /* 가입하기 클릭 */
			String signUpInputXpath2 ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/div[5]";
			WebElement signUpInput2 = driverTwitter.findElement(By.xpath(signUpInputXpath2));
	        actions.moveToElement(signUpInput2).perform();
	        ThreadUtil.sleepSec(3);
	        actions.click().perform();
	                
	        // 이메일 사용하기 클릭
	        String changeEmailUseXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div[2]/div[3]";
	        WebElement changeEmailUse = driverTwitter.findElement(By.xpath(changeEmailUseXpath));
	        actions.moveToElement(changeEmailUse).perform();
	        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", changeEmailUse Click" );
	        actions.click().perform();
	        ThreadUtil.sleepSec(3);
	        
	        String email = "tapa2311149627";
	        /* click 누른 후 아이디 입력창으로 이동*/
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	                
	        actions.sendKeys(email).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        
	        actions.sendKeys(email).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        
	        Random random = new Random();
	        
	        String randomD = Integer.toString(random.nextInt(20)+1);
	        actions.sendKeys(randomD).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        String randomM = Integer.toString(random.nextInt(11)+1);
	        actions.sendKeys(randomM).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        String randomY = Integer.toString(random.nextInt(25)+1960);
	        actions.sendKeys(randomY).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(2);

	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.TAB).perform();
	        actions.sendKeys(Keys.ENTER).perform();
	        ThreadUtil.sleepSec(3);
	        
	        /* 다음버튼 클릭*/
//			String nextInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div";
//			WebElement nextInput = driverTwitter.findElement(By.xpath(nextInputXpath));
//	        actions.moveToElement(nextInput).perform();
//	        ThreadUtil.sleepSec(1);
//	        actions.click().perform();
	        
	        /* 가입 클릭*/
//			String signUpComplateInputXpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div";
//			WebElement signUpComplateInput = driverTwitter.findElement(By.xpath(signUpComplateInputXpath));
//	        actions.moveToElement(signUpComplateInput).perform();
//	        ThreadUtil.sleepSec(1);
//	        actions.click().perform();
	        	        
	        actions.click().perform();
	        actions.sendKeys(Keys.TAB).perform();
	        ThreadUtil.sleepSec(3);
	        actions.sendKeys(Keys.TAB).perform();
	        
	        String name ="tapa2311149625@ruu.kr";
	        String code = AuthenticateEmail(name);
	        
	        if(code.isEmpty()) {
	        	logger.error("certificationNumber is NULL change Proxy IP and host OR ruu.kr Site Status Check");
	        	System.exit(1);
	        }
	        ThreadUtil.sleepSec(1);
	        actions.sendKeys(code).perform();
	        abortRuuKrDriver();
	        
	        /* 다음버튼 클릭 2 */
			String nextInput2Xpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div";
			WebElement nextInput2 = driverTwitter.findElement(By.xpath(nextInput2Xpath));
	        actions.moveToElement(nextInput2).perform();
	        ThreadUtil.sleepSec(1);
	        actions.click().perform();
	        
	        ThreadUtil.sleepSec(3);
	        
	        actions.sendKeys("tapaman1234").perform();
	        
	        /* 다음버튼 클릭 3 */
			String nextInput3Xpath ="/html/body/div/div/div/div[1]/div[2]/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div";
			WebElement nextInput3 = driverTwitter.findElement(By.xpath(nextInput3Xpath));
	        actions.moveToElement(nextInput3).perform();
	        ThreadUtil.sleepSec(1);
	        actions.click().perform();	        
	        ThreadUtil.sleepSec(5);
	        
	        ThreadUtil.sleepSec(10000);
	        
		       /* 프로필 설정 화면 간헐적 등장*/
	        if(driverTwitter.getCurrentUrl().equals("https://twitter.com/i/flow/signup")) {
	        	actions.keyDown(Keys.CONTROL).sendKeys(Keys.F5).keyUp(Keys.CONTROL).perform(); //새로고침
	        	ThreadUtil.sleepSec(5);
	        	
		        String userNameXpath = "/html/body/div[1]/div/div/div[2]/header/div/div/div/div[2]/div/div/div[2]/div/div[2]/div/div/div/span";
		        WebElement userNameEl = driverTwitter.findElement(By.xpath(userNameXpath));
		        String userName = userNameEl.getText()
		        		.replaceAll("\\@", "");
		        
	            System.out.println("email : " + email);
	            System.out.println("userId : " + userName);
	            System.out.println("password : tapaman1234");
	            System.out.println("SignUp OK");
	        }
	        
        }catch (Exception e) {
			e.printStackTrace();
		}
	                        
//	            abortTwitterDriver();
	}
	public static Map.Entry<String, Integer> distinguishIpAndProt(int proxyIpListIndex) {
		String[] ipAddress = proxyIpList.get(proxyIpListIndex).split(":");
		String proxyIp = ipAddress[0];
		int port = Integer.parseInt(ipAddress[1]);
		return new AbstractMap.SimpleEntry<>(proxyIp, port);
	}
}

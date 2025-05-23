package com.tapacross.sns.crawler.twitter;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapacross.sns.crawler.twitter.service.TwitterLoginInfoService;
import com.tapacross.sns.util.FileUtil;
import com.tapacross.sns.util.ThreadUtil;

public class proxyWebDriver {
	private WebDriver driver;
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private final Logger logger = LoggerFactory.getLogger(proxyWebDriver.class);
	private static int ProxyIpListSize = 0;

	private static List<String> proxyIpList = new ArrayList<>();
	private static int ProxyIpListindex = 0;
	public static void main(String[] args) {
		List<HashMap<String, Integer>> ipList = new ArrayList<>(); 
		
		proxyWebDriver pwdClass = new proxyWebDriver();
		String proxyPath ="target/classes/data/haiip.txt";		
		try {
			String lines = FileUtil.readFromFile(proxyPath, "\r\n");
			String[] splitStrings = lines.split("\r\n");
			
			for (String splitString : splitStrings ) {
				proxyIpList.add(splitString);
				String[] ipAddress = splitString.split(":");
				String proxyIp = ipAddress[0];
				int port = Integer.parseInt(ipAddress[1]);	
				HashMap<String, Integer> proxyIpInfo = new HashMap<String, Integer>();
				proxyIpInfo.put(proxyIp, port);
				ipList.add(proxyIpInfo);
				
//				String ip = proxyIpInfo.keySet().toString()
//						.replaceAll("\\]", "")
//						.replaceAll("\\[", "");
//				int portA = Integer.parseInt(proxyIpInfo.values().toString()
//						.replaceAll("\\]", "")
//						.replaceAll("\\[", ""));
			}
			System.out.println(ipList.get(0));
			Collections.reverse(ipList);
			System.out.println(ipList.get(0));
			for(HashMap<String, Integer> ipInfo: ipList) {
				System.out.println(ipInfo);
				String ip = ipInfo.keySet().toString()
						.replaceAll("\\]", "")
						.replaceAll("\\[", "");
				int port = Integer.parseInt(ipInfo.values().toString()
						.replaceAll("\\]", "")
						.replaceAll("\\[", ""));
				
				if(pwdClass.driver == null) {
					System.out.println("driver is null");
					pwdClass.initDriver(ip, port);
				}else {				
					while(true) {
						System.out.println("driver is check....");
						ThreadUtil.sleepSec(1);
						if(pwdClass.driver == null) {
							System.out.println("while in drvier is null");
							pwdClass.initDriver(ip, port);
							continue;
						}
					}
				}				
			}
						
//			ProxyIpListSize = proxyIpList.size() - 1;		
//			Random random = new Random();
//			ProxyIpListindex = random.nextInt(proxyIpList.size() -1);
//			Map.Entry<String, Integer> proxyIpInfo = pwdClass.distinguishIpAndProt(ProxyIpListindex);
//			String proxyIp = proxyIpInfo.getKey();
//			int port = proxyIpInfo.getValue();

//			System.out.println(proxyIp+":"+port);
//			System.out.println("프록시사용 일시중단");
//			pwdClass.initDriver(proxyIp, port);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void initDriver(String proxyIp, int port) { // 프록시 작업할때 사용 		
		try {
			this.driver = null;
			if(this.driver == null) {
				ChromeOptions options = new ChromeOptions();
//		        options.addArguments("--headless"); // 크롬창 숨기기, javascript가 감지가능 
		        options.addArguments("--use-fake-ui-for-media-stream");
		        options.addArguments("--use-fake-device-for-media-stream");
//				options.addArguments("--blink-settings=imagesEnabled=false"); // 이미지차단
		        options.addArguments("--incognito"); // 이미지를 로딩 x
		        options.addArguments("--disable-cache");
		        options.addArguments("--disable-cookies");
				options.addArguments("disable-infobars");// 크롬브라우저 정보 바 비활성
				options.addArguments("--no-sandbox"); // 샌드박스 비활성화
				options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않고 일번적인 디스크 기반의 tmpfs를 사용
				options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 컨트롤이 가능 비활성화
				options.setExperimentalOption("useAutomationExtension", false); // 자동화 확장프로그램 비활성화
				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //enable-automation스위치를 제외하고 실행합니다.
		        options.addArguments("--remote-debugging-address=127.0.0.1"); 
		        options.addArguments("--remote-debugging-port=9222"); 
				options.addArguments("Sec-Fetch-Site=same-origin");
				options.addArguments("Sec-Fetch-Site=same-origin");
//		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
		        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");										        
		        Proxy proxy = new Proxy();
//		        proxy.setHttpProxy(proxyIp + ":" + port);
//		        proxy.setSslProxy(proxyIp + ":" + port);
		        
		        
//		        options.addArguments("--proxy-server=http://" + proxyIp + ":" + port);
		        
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", driver init start");
				String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
				String WEB_DRIVER_PATH = "C:\\work\\chromedriver.exe"; // 드라이버 경로
				System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
				try {
					this.driver = new ChromeDriver(options);
//					this.driver = new ChromeDriver();
//					this.driver.get("https://twitter.com/i/flow/login");
					this.driver.get("https://www.instagram.com");
					
				} catch (Exception e) {
					logger.error(TWITTER_LOGIN_INFO_MANAGER + ", driver new ChromeDriver(options Error)");
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
		
        // 주기적으로 브라우저 상태 확인
        while (true) {
            // 브라우저가 닫혔는지 여부 확인
        try {
            if (!driver.getWindowHandles().isEmpty()) {
            	ThreadUtil.sleepSec(1);
            } else {
                System.out.println("브라우저가 닫혔습니다.");
                this.driver = null;
                break;
            }
        }catch (Exception e) {
			// TODO: handle exception
            System.out.println("브라우저가 닫혔습니다.");
            this.driver = null;
            break;
		}

        }
        
//        abort();
		
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
	
	public Map.Entry<String, Integer> distinguishIpAndProt(int proxyIpListIndex) {
		String[] ipAddress = proxyIpList.get(proxyIpListIndex).split(":");
		String proxyIp = ipAddress[0];
		int port = Integer.parseInt(ipAddress[1]);		
		
		Random random = new Random();
		ProxyIpListindex = random.nextInt(proxyIpList.size() -1);
		return new AbstractMap.SimpleEntry<>(proxyIp, port);
	}
}

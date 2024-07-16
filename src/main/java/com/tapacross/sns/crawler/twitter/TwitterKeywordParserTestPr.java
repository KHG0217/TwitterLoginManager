package com.tapacross.sns.crawler.twitter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.tapacross.sns.crawler.twitter.parser.TwitterAPIKeywordParserImpl;
import com.tapacross.sns.crawler.twitter.service.TwitterLoginInfoService;
import com.tapacross.sns.entity.TBTwitterAuthToken;
import com.tapacross.sns.util.FileUtil;
import com.tapacross.sns.util.ThreadUtil;

public class TwitterKeywordParserTestPr {
	private TwitterLoginInfoService service;
	private Queue<String> keywordQueue = new LinkedList<String>();
	private static final String TWITTER_LOGIN_INFO_MANAGER = "twitter-login-info-manager";
	private final Logger logger = LoggerFactory.getLogger(TwitterKeywordParserTestPr.class);
	
	public static void main(String[] args) {
		TwitterKeywordParserTestPr pr = new TwitterKeywordParserTestPr();
		pr.initSpringBeans();	
		
		Long sleepMillis = 10000L;
		String selectIp = "61.97.191.109";
		List<TBTwitterAuthToken> useList = new ArrayList<TBTwitterAuthToken>();
				
		while(true) {
			
			if(useList.size() == 0) {
				System.out.println("useList.size zero, call method returnUseAuthList");
				useList = pr.returnUseAuthList("61.97.191.109");
				ThreadUtil.sleepSec(10);
			}
			
			for(TBTwitterAuthToken auth: useList) {
				
				pr.testSearchRecentTweetsByKeywordByParser(auth, sleepMillis);
			}
			useList.clear();
			
		}
		
	}
	
	private void initSpringBeans() {
		@SuppressWarnings("resource")
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:spring/application-context.xml");		
		this.service = context.getBean(TwitterLoginInfoService.class);		
	}
	
	public void testSearchRecentTweetsByKeywordByParser(TBTwitterAuthToken auth, long sleepMillis) {  
		TwitterAPIKeywordParserImpl parser = new TwitterAPIKeywordParserImpl();
		String xClient = null;

		
		while(true) {
			try {		
				
				if(keywordQueue.size() == 0) {
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", push keyword to keywordQueue");
					pushKeywordQueue();
				}
				
				String keyword = keywordQueue.poll();
				Response res = parser.searchRecentTweets_AccountUse(keyword, auth.token, auth.cookie, xClient);
				ThreadUtil.sleep(sleepMillis);
		        // 현재 날짜와 시간을 가져옴
		        Date now = new Date();
		        
		        // 출력 형식을 설정
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        // 형식에 맞게 날짜와 시간을 문자열로 변환
		        String formattedDate = sdf.format(now);
		        
		        // 콘솔에 출력
		        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", now date: " + formattedDate + ", KEYWORD: " + keyword + ", USER ID: " + auth.userId);
		        		        
		        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", res status: " + res.statusCode());
				
				if(res.body().contains("Denied by access control")) { // id block 에러
					logger.error("Denied by access email, update Status Z, " + auth.userId);
					service.updateAuthToken401(auth.userId);
					logger.info("Change Status value is Z, " + auth.userId);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", now date: " + formattedDate);
					break;
				}
				
				if(res.statusCode() == 401) {
					service.updateAuthToken401(auth.userId);
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", now date: " + formattedDate);
					break;

				}
				if(res.statusCode() == 429 && res.body().contains("Rate limit exceeded")) {
					LocalDateTime currentTime = LocalDateTime.now();
					long unixTimestamp = Long.parseLong(res.header("x-rate-limit-reset")); // Unix timestamp 값
//					System.out.println(unixTimestamp);
				    
					// Unix timestamp를 Instant로 변환
			        Instant instant = Instant.ofEpochSecond(unixTimestamp);

			        // Instant를 LocalDateTime으로 변환
			        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					
			        Duration duration = Duration.between(currentTime, dateTime);
			        long secondsToWait = duration.getSeconds();
			        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", secondsToWait: " + secondsToWait);
							        
			        long minutes = secondsToWait / 60; 
			        long remainingSeconds = secondsToWait % 60;
			        
			        logger.info(TWITTER_LOGIN_INFO_MANAGER + ", now time: " + formattedDate + ", remaining sleep: " + minutes + "min " + remainingSeconds + "sec + 5sec...");						
					secondsToWait = secondsToWait + 5;
					secondsToWait = secondsToWait * 1000;

					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					
					long newTimeInMillis = currentTimestamp.getTime() + secondsToWait;
					Timestamp newTimestamp = new Timestamp(newTimeInMillis);
					
					auth.useDate = newTimestamp;
					
					service.updateAuthTokenUseDateReset(auth);	
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", update auth token use date: "  + auth.useDate);
					break;
				}
				
				if (res.body().length() > 100) {
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", "  + res.body().substring(0, 200));
				} else {
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", "  + res.body());
				}
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", x-rate-limit-limit: "  + res.header("x-rate-limit-limit"));
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", x-rate-limit-reset:"  + res.header("x-rate-limit-reset"));
				logger.info(TWITTER_LOGIN_INFO_MANAGER + ", x-rate-limit-remaining: "  + res.header("x-rate-limit-remaining"));
//				System.out.println("x-response-time:" + res.header("x-response-time"));
//				System.out.println("x-response-time:" + res.header("x-response-time"));

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(TWITTER_LOGIN_INFO_MANAGER + ", " + e.getMessage());
			}
		}

	}
	
	public List<TBTwitterAuthToken> returnUseAuthList(String ip){
		List<TBTwitterAuthToken> useList = new ArrayList<>();
		try {
			List<TBTwitterAuthToken> allAuthList = service.selectIpAuthInfoList(ip);
			
			useList = new ArrayList<TBTwitterAuthToken>();
//			System.out.println(testAuthList.get(0));
			
			for(TBTwitterAuthToken auth : allAuthList) {
				if(!auth.status.equals("T")) {
					logger.info(TWITTER_LOGIN_INFO_MANAGER + ", status not T, pass, id: "  + auth.userId + ", status: " + auth.status);
					continue;
				}
				
		        // 현재 날짜와 시간을 가져옴
		        Date now = new Date();
		        
		        // 출력 형식을 설정
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        // 형식에 맞게 날짜와 시간을 문자열로 변환
		        String formattedDate = sdf.format(now);
		        	
		        if(auth.useDate.after(now)) {
		        	logger.info(TWITTER_LOGIN_INFO_MANAGER + ", time is not available, pass, id: "   + auth.userId + ", use Date: " + auth.useDate + ", now date: " + formattedDate );
		        	continue;
		        }      
		        useList.add(auth);
			}
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", available auth size/totla auth size: " + useList.size()+ "/" + allAuthList.size());			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", " + e.getMessage());
		}

		return useList;
	}
	
	public void pushKeywordQueue() {
		try {
			String keywordPath ="./twitter.keyword.txt";
			String lines = FileUtil.readFromFile(keywordPath, "\n");
			String[] splitStrings = lines.split("\n");
			for (String splitString : splitStrings ) {
				keywordQueue.add(splitString);
			}
			
			logger.info(TWITTER_LOGIN_INFO_MANAGER + ", pusu keyword count : "  + keywordQueue.size());
			
		}catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			logger.error(TWITTER_LOGIN_INFO_MANAGER + ", " + e.getMessage());
		}
	}
}

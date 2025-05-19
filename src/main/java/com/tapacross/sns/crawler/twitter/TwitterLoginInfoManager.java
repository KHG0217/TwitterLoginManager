package com.tapacross.sns.crawler.twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tapacross.sns.crawler.twitter.service.TwitterLoginInfoService;
import com.tapacross.sns.entity.TBTwitterAuthToken;

/*
 * 트위터 계정을 새로 추가하거나 업데이트한다.
 * 크롬드라이버 버전 = 114
 * type = 1 : insert
 * type = 2 : update
 *
 * 현재 프록시는 사용하지 않는다.
 * */
public class TwitterLoginInfoManager {
	private final Logger logger = LoggerFactory.getLogger(TwitterLoginInfoManager.class);
	private TwitterLoginInfoService service;
	private static int insertCount = 0;
	private static int insertFailCount = 0;
	private static List<String> insertfailList = new ArrayList<>();
	

	public static void main(String[] args) { 		
		TwitterLoginInfoManager manger = new TwitterLoginInfoManager();

		try {
			manger.logger.info("type select, 1: insert, 2: update, :");
			Scanner scanner = new Scanner(System.in);
			int number = scanner.nextInt();
			scanner.close();

			TwitterMangerUtil util = new TwitterMangerUtil();
			util.initSpringBeans();

			String driverPath = "C:\\work\\chromedriver.exe"; // chromedriver가 저장되어있는 경로
			String date = util.currentDate();
			manger.logger.info("----------------CURRENT DATE: {}-------------", date);
			switch (number) {
			case 1:
				manger.logger.info("----------------INSERT START---------------------------------");
				List<TBTwitterAuthToken> newTwitterAuthList = util.insertNewTwitterAuthList();
				if(newTwitterAuthList.size() == 0) {
					manger.logger.info("newTwitterAuthList size is 0");
					return;
				}

				for(TBTwitterAuthToken auth : newTwitterAuthList) {
					String profileDir = util.createProfileDir();
					ChromeDriver driver = util.init(driverPath, profileDir);

					Map<String, String> loginInfo = util.selectTrsfAndCookie(driver,
							auth.userId, auth.userPassword, auth.email);
					if(loginInfo == null) {
						manger.logger.error("loginInfo is null check auth status, id: {}, pwd: {}", auth.userId, auth.userPassword);
						util.insertfailList.add(auth.userId);
						util.insertFailCount ++;
					}else {
						util.insertNewTwitterAuth(auth,loginInfo);
					}
					util.abort(driver, profileDir);
				}
				manger.logger.info("----------------INSERT END---------------------------------");
				manger.logger.info("insertCount: {}",insertCount);
                manger.logger.info("insertFailCount : {}", insertFailCount);
				manger.logger.info("insertFail list : ");
				for(String failId : insertfailList) {
					manger.logger.info(failId);
				}
				break;
				
			case 2:
				manger.logger.info("----------------UPDATE START---------------------------------");
				String status = "X"; // 현재 수동 업데이트하는 상태값은 X
				List<TBTwitterAuthToken> updateList = util.selectTwitterAuthList(status);
				for(TBTwitterAuthToken auth : updateList) {
					String profileDir = util.createProfileDir();
					ChromeDriver driver = util.init(driverPath, profileDir);

					Map<String, String> loginInfo = util.selectTrsfAndCookie(driver,
							auth.userId, auth.userPassword, auth.email);
					if(loginInfo == null) {
                        manger.logger.error("loginInfo is null check auth status, id: {}, pwd: {}", auth.userId, auth.userPassword);
						util.updatefailList.add(auth.userId);
						util.updateFailCount ++;
					}else {
						util.updateTwitterAuthData(auth,loginInfo);
					}
					util.abort(driver, profileDir);
				}
				System.out.println("----------------UPDATE END---------------------------------");
                manger.logger.info("updateCount : {}", util.updateCount);
                manger.logger.info("updateFailCount : {}", util.updateFailCount);
				for(String failId : util.updatefailList) {
					manger.logger.info(failId);
				}
				break;

			default:
				manger.logger.info("arg is 1 or 2...SYSTEM EXIT...");
				System.exit(1);
				break;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
package com.tapacross.sns.crawler.twitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapacross.sns.entity.TBTwitterAuthToken;
import com.tapacross.sns.util.ThreadUtil;

public class TwitterXCTXIDTokenCrawler {
	private final Logger logger = LoggerFactory.getLogger(TwitterXCTXIDTokenCrawler.class);
	
	private static TwitterMangerUtill utill;
	
	
	public static void main(String[] args) {
		TwitterXCTXIDTokenCrawler parser = new TwitterXCTXIDTokenCrawler();
		utill = new TwitterMangerUtill();
		
		int insertCount = 11; // 한계정으로 넣을 토큰수, 계정 블럭이 발생할 수 있으므로 40회는 넘기지 않는다.
		List<String> targetIpList = parser.selectTargetIp();
		if(targetIpList.size() == 0) System.exit(1);
		
		utill.initSpringBeans();
		List<TBTwitterAuthToken> authList = null;
		try {
			String status = "K"; // XCTxid토큰 업데이트용 계정
			authList = utill.selectTwitterAuthList(status);
			if(authList.size() == 0) {
				parser.logger.info("authList size is null, system exit");
				System.exit(1);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		for(String ip : targetIpList) {
			parser.logger.info("add TwitterXCTXIDToken, ip: " + ip);
			
			for(TBTwitterAuthToken auth : authList) {
				String profileDir = utill.createProfileDir();
				ChromeDriver driver = utill.init(null, 0, profileDir);
				Map<String, String> loginInfo = utill.selectTrsfAndCookie(driver, 
						auth.userId, auth.userPassword, auth.email, null, 0);
				if(loginInfo == null) {
					parser.logger.error("loginInfo is null check auth status, "
							+ "id: " + auth.userId + ", pwd: " + auth.userPassword);
					utill.abort(driver, profileDir);
					System.exit(1);
				}
				utill.parseAndInsertXCTXIDToken(driver, ip, insertCount, profileDir);
				utill.abort(driver, profileDir);
				ThreadUtil.sleepSec(5);
			}
		}

		
	}
	
	private List<String> selectTargetIp() {
		List<String> targetIpList = new ArrayList<String>();
		String path = "../twitter-login-info-manager/src/main/resources/data/xctxid_token_target_ips.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					targetIpList.add(line);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		logger.info("targetIpList size = " + targetIpList.size());
		return targetIpList;
	}
	
	
}

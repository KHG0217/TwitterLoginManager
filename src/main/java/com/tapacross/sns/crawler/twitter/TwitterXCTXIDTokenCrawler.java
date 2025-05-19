package com.tapacross.sns.crawler.twitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapacross.sns.entity.TBTwitterAuthToken;
import com.tapacross.sns.util.ThreadUtil;

/**
 * 트위터 Xct-xid 토큰을 수집하고, IP별로 DB에 추가한다.
 * 한 계정당 연속으로 토큰을 호출하여 수집한다.
 * 이때, 연속으로 호출을 할 경우 블럭이 발생할 수 있으므로 정책에따라 조정한다. (한 계정당 연속 호출 가능 수최대 8회 - 25.05.19)
 * 연속 호출 후 3분의 대기시간이 있다면 블럭이 발생하지 않는다. (25.05.19)
 * 토큰 수집용 계정은 status = 'K' 의 계정들을 사용한다.
 */
public class TwitterXCTXIDTokenCrawler {
	private final Logger logger = LoggerFactory.getLogger(TwitterXCTXIDTokenCrawler.class);
	
	private static TwitterMangerUtil util;
	
	
	public static void main(String[] args) {
		TwitterXCTXIDTokenCrawler parser = new TwitterXCTXIDTokenCrawler();
		util = new TwitterMangerUtil();
		
		int insertCount = 8; // 한계정으로 연속으로 넣을 토큰수, 계정 블럭 발생에 주의한다.
		List<String> targetIpList = parser.selectTargetIp();
		if(targetIpList.size() == 0) System.exit(1);

		util.initSpringBeans();
		List<TBTwitterAuthToken> authList = null;
		try {
			String status = "K"; // XCTxid토큰 업데이트용 계정
			authList = util.selectTwitterAuthList(status);
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
				String profileDir = util.createProfileDir();
				String driverPath = "C:\\work\\chromedriver.exe";
				ChromeDriver driver = util.init(driverPath, profileDir);
				Map<String, String> loginInfo = util.selectTrsfAndCookie(driver,
						auth.userId, auth.userPassword, auth.email);
				if(loginInfo == null) {
					parser.logger.error("loginInfo is null check auth status, "
							+ "id: " + auth.userId + ", pwd: " + auth.userPassword);
					util.abort(driver, profileDir);
					util.forceKillChromeDriverOnWindows();
					System.exit(1);
				}
				util.parseAndInsertXCTXIDToken(driver, ip, insertCount, profileDir);
				util.abort(driver, profileDir);
				util.forceKillChromeDriverOnWindows();
				ThreadUtil.sleepSec(5);
			}
		}

		
	}

	/**
	 * 토큰을 넣을 IP 리스트를 반환한다.
	 * 토큰을 추가하고자 한다면 xctxid_token_target_ips.txt에 ip를 추가한다.
	 * @return
	 */
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

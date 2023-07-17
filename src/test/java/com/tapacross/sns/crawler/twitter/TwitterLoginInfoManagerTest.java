package com.tapacross.sns.crawler.twitter;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/application-context.xml")
public class TwitterLoginInfoManagerTest {

	@Test
	public void testSelectTrsfAndCookie() {
		TwitterLoginInfoManager manger = new TwitterLoginInfoManager();
		String id = "cuckoo05530671";
		String password = "tapaman1234";
		String email ="cuckoo055@ruu.kr";
		String proxyIp ="49.254.132.223";
		int port = 5407;
		Map<String,String> loginInfo = manger.selectTrsfAndCookie(id, password, email, proxyIp, port);		
		System.out.println("loginInfo : " + loginInfo);
	}
}

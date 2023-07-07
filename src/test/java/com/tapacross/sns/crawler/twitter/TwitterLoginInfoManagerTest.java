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
		String id = "tapa23070458297";
		String password = "tapaman1234";
		Map<String,String> loginInfo = manger.selectTrsfAndCookie(id, password);		
		System.out.println("loginInfo : " + loginInfo);
	}
}

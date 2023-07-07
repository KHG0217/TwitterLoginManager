package com.tapacross.sns.crawler.twitter.service

import com.tapacross.sns.crawler.twitter.TwitterLoginInfoManager
import com.tapacross.sns.entity.TBTwitterAuthToken
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/application-context.xml")
class TestTwitterLoginInfoService {
	@Autowired
	private TwitterLoginInfoService service;
	
	@Test
	public void testSelectExistTwitterId () {
		String email = "cuckoo039@ruu.krsdsdsd"
		String data = service.selectExistTwitterId(email)
		println data
	}
	
	@Test
	public void testInsertTwitterNewId () {
		TBTwitterAuthToken entity = new TBTwitterAuthToken()
		entity.email ="protest223";
		entity.userId ="protest";
		entity.userPassword ="protest";
		entity.token ="protest";
		entity.cookie ="protest";
		
		service.insertTwitterNewId(entity)
		
	}
	
	@Test
	public void testSelectStatusFTwitterIdList() {
		List<TBTwitterAuthToken> statusFList = new ArrayList();
		statusFList = service.selectStatusFTwitterIdList()
		
		for (TBTwitterAuthToken data : statusFList) {
			println data
		}
	}
	
	@Test
	public void testUpdateTwitterAuthData() {
		List<TBTwitterAuthToken> statusFList = new ArrayList();
		statusFList = service.selectStatusFTwitterIdList()
		
		TwitterLoginInfoManager manager = new TwitterLoginInfoManager()
		
		for(TBTwitterAuthToken data : statusFList) {
			String id = data.userId
			String password = data.userPassword
			
			
			Map<String,String> loginInfo = manager.selectTrsfAndCookie(id, password)
			
			if(loginInfo == null) {
				continue;
			}
			
			
			TBTwitterAuthToken entity = new TBTwitterAuthToken()
			entity.email = data.email;
			entity.userId = id;
			entity.userPassword =password;
			entity.token =loginInfo.get("crsfToken");
			entity.cookie =loginInfo.get("cookie");
			
			service.updateTwitterAuthData(entity)
		}

		
		
	}

	
}

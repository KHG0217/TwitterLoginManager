package com.tapacross.sns.crawler.twitter.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

import com.tapacross.sns.crawler.twitter.dao.TwitterLoginInfoDAO
import com.tapacross.sns.entity.TBTwitterAuthToken

interface LoginInfoService {
	def String selectExistTwitterId(String email) throws DataAccessException
	def void insertTwitterNewId (TBTwitterAuthToken entity) throws DataAccessException
	def List<TBTwitterAuthToken> selectStatusFTwitterIdList () throws DataAccessException
	def void updateTwitterAuthData (TBTwitterAuthToken entity) throws DataAccessException
}

@Service
class TwitterLoginInfoService implements LoginInfoService {

	@Autowired
	private TwitterLoginInfoDAO TwitterDao;
	
	@Override
	public String selectExistTwitterId(String email) throws DataAccessException {
		return TwitterDao.selectExistTwitterId(email)
	}

	@Override
	public void insertTwitterNewId(TBTwitterAuthToken entity) 
	throws DataAccessException {
				
		TwitterDao.insertTwitterNewId(entity)
		println "insert in DB : $entity.email"
	}

	@Override
	public List<TBTwitterAuthToken> selectStatusFTwitterIdList() throws DataAccessException {
		return TwitterDao.selectStatusFTwitterIdList()
	}
	
	@Override
	public void updateTwitterAuthData(TBTwitterAuthToken entity) throws DataAccessException {
		TwitterDao.updateTwitterAuthData(entity)	
		println "update in DB : $entity.email"
	}


}

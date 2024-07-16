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
	def void updateAuthTokenStatusC (String email) throws DataAccessException
	def void updateAuthTokenStatusE (String email) throws DataAccessException
	def void updateAuthTokenStatusF (String email) throws DataAccessException
	def List<TBTwitterAuthToken> selectIpAuthInfoList (String ip) throws DataAccessException
	def void updateAuthToken401 (String id) throws DataAccessException
	def List<TBTwitterAuthToken> select401TwitterId () throws DataAccessException
	def void updateAuthTokenUseDateReset (TBTwitterAuthToken entity) throws DataAccessException
	
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

	@Override
	public void updateAuthTokenStatusC(String email) throws DataAccessException {
		TwitterDao.updateAuthTokenStatusC(email)
		println "update status C : $email"
		
	}

	@Override
	public void updateAuthTokenStatusE(String email) throws DataAccessException {
		TwitterDao.updateAuthTokenStatusE(email)
		println "update status E : $email"	
	}

	@Override
	public void updateAuthTokenStatusF(String email) throws DataAccessException {
		TwitterDao.updateAuthTokenStatusF(email)
		println "update status F : $email"	
	}

	@Override
	public List<TBTwitterAuthToken> selectIpAuthInfoList(String ip) throws DataAccessException {
		// TODO Auto-generated method stub
		return TwitterDao.selectIpAuthInfoList(ip)
		
	}

	@Override
	public void updateAuthToken401(String id) throws DataAccessException {
		TwitterDao.updateAuthToken401(id);
		println "updateAuthToken 401 error, $id"
		
	}

	@Override
	public List<TBTwitterAuthToken> select401TwitterId() throws DataAccessException {
		// TODO Auto-generated method stub
		return TwitterDao.select401TwitterId()
		
	}

	@Override
	public void updateAuthTokenUseDateReset(TBTwitterAuthToken entity) throws DataAccessException {
		// TODO Auto-generated method stub
		TwitterDao.updateAuthTokenUseDateReset(entity)
		
	}


}

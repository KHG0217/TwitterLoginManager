package com.tapacross.sns.crawler.twitter.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

import com.tapacross.sns.crawler.twitter.dao.TwitterLoginInfoDAO
import com.tapacross.sns.crawler.twitter.entity.TBTwitterXCTxidToken
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
	def void insertXCTxidToken (TBTwitterXCTxidToken entity) throws DataAccessException
	def List<TBTwitterAuthToken> selectAuthTokenStatus (String status) throws DataAccessException
	def List<TBTwitterAuthToken> selectAuthTokenIpAndStatus (String ip, String status) throws DataAccessException
	
	
}

@Service
class TwitterLoginInfoService implements LoginInfoService {

	@Autowired
	private TwitterLoginInfoDAO twitterDao;
	
	@Override
	public String selectExistTwitterId(String email) throws DataAccessException {
		return twitterDao.selectExistTwitterId(email)
	}

	@Override
	public void insertTwitterNewId(TBTwitterAuthToken entity) 
	throws DataAccessException {
				
		twitterDao.insertTwitterNewId(entity)
		println "insert in DB : $entity.email"
	}

	@Override
	public List<TBTwitterAuthToken> selectStatusFTwitterIdList() throws DataAccessException {
		return twitterDao.selectStatusFTwitterIdList()
	}
	
	@Override
	public void updateTwitterAuthData(TBTwitterAuthToken entity) throws DataAccessException {
		twitterDao.updateTwitterAuthData(entity)	
		println "update in DB : $entity.email"
	}

	@Override
	public void updateAuthTokenStatusC(String email) throws DataAccessException {
		twitterDao.updateAuthTokenStatusC(email)
		println "update status C : $email"
		
	}

	@Override
	public void updateAuthTokenStatusE(String email) throws DataAccessException {
		twitterDao.updateAuthTokenStatusE(email)
		println "update status E : $email"	
	}

	@Override
	public void updateAuthTokenStatusF(String email) throws DataAccessException {
		twitterDao.updateAuthTokenStatusF(email)
		println "update status F : $email"	
	}

	@Override
	public List<TBTwitterAuthToken> selectIpAuthInfoList(String ip) throws DataAccessException {
		// TODO Auto-generated method stub
		return twitterDao.selectIpAuthInfoList(ip)
		
	}

	@Override
	public void updateAuthToken401(String id) throws DataAccessException {
		twitterDao.updateAuthToken401(id);
		println "updateAuthToken 401 error, $id"
		
	}

	@Override
	public List<TBTwitterAuthToken> select401TwitterId() throws DataAccessException {
		// TODO Auto-generated method stub
		return twitterDao.select401TwitterId()
		
	}

	@Override
	public void updateAuthTokenUseDateReset(TBTwitterAuthToken entity) throws DataAccessException {
		// TODO Auto-generated method stub
		twitterDao.updateAuthTokenUseDateReset(entity)
		
	}

	@Override
	public void insertXCTxidToken(TBTwitterXCTxidToken entity) throws DataAccessException {
		// TODO Auto-generated method stub
		twitterDao.insertXCTxidToken(entity)
		
	}

	@Override
	public List<TBTwitterAuthToken> selectAuthTokenStatus(String status) throws DataAccessException {
		// TODO Auto-generated method stub
		return twitterDao.selectAuthTokenStatus(status)
	}

	@Override
	public List<TBTwitterAuthToken> selectAuthTokenIpAndStatus(String ip, String status) throws DataAccessException {
		// TODO Auto-generated method stub
		return twitterDao.selectAuthTokenStatus(ip, status)
	}


}

package com.tapacross.sns.crawler.twitter.dao

import org.mybatis.spring.SqlSessionTemplate
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Repository
import com.tapacross.sns.crawler.twitter.entity.TBTwitterXCTxidToken
import com.tapacross.sns.entity.TBTwitterAuthToken

import javax.annotation.Resource

interface loginInfoDAO {
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

@Repository
class TwitterLoginInfoDAO implements loginInfoDAO  {
	
	@Resource(name = "sqlSessionTemplate")
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public String selectExistTwitterId(String email) throws DataAccessException {
		return sqlSessionTemplate.selectOne("sql.resources.twitterloginInfodao.selectExistTwitterId", email)
	}

	@Override
	public void insertTwitterNewId(TBTwitterAuthToken entity) throws DataAccessException {
		 sqlSessionTemplate.insert("sql.resources.twitterloginInfodao.insertTwitterNewId", entity)
		
	}

	@Override
	public List<TBTwitterAuthToken> selectStatusFTwitterIdList() throws DataAccessException {
		return sqlSessionTemplate.selectList("sql.resources.twitterloginInfodao.selectStatusFTwitterId")
	}
	
	@Override
	public void updateTwitterAuthData(TBTwitterAuthToken entity) throws DataAccessException {
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateTwitterAuthData", entity)
		
	}

	@Override
	public void updateAuthTokenStatusC(String email) throws DataAccessException {
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateAuthTokenStatusC", email)		
	}

	@Override
	public void updateAuthTokenStatusE(String email) throws DataAccessException {
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateAuthTokenStatusE", email)
	}

	@Override
	public void updateAuthTokenStatusF(String email) throws DataAccessException {
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateAuthTokenStatusF", email)

	}

	@Override
	public List<TBTwitterAuthToken> selectIpAuthInfoList(String ip) throws DataAccessException {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectList("sql.resources.twitterloginInfodao.selectIpAuthInfo", ip)
	}
	
	@Override
	public void updateAuthToken401(String id) throws DataAccessException {
		// TODO Auto-generated method stub
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateAuthToken401", id)	
	}

	@Override
	public List<TBTwitterAuthToken> select401TwitterId() throws DataAccessException {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectList("sql.resources.twitterloginInfodao.select401TwitterId")
		
	}

	@Override
	public void updateAuthTokenUseDateReset(TBTwitterAuthToken entity) throws DataAccessException {
		// TODO Auto-generated method stub
		sqlSessionTemplate.update("sql.resources.twitterloginInfodao.updateAuthTokenUseDateReset", entity)
		
		
	}

	@Override
	public void insertXCTxidToken(TBTwitterXCTxidToken entity) throws DataAccessException {
		// TODO Auto-generated method stub
		sqlSessionTemplate.insert("sql.resources.twitterloginInfodao.insertXCTxidToken", entity)
	}

	@Override
	public List<TBTwitterAuthToken> selectAuthTokenStatus(String status) throws DataAccessException {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectList("sql.resources.twitterloginInfodao.selectAuthTokenStatus", status)
	}

	@Override
	public List<TBTwitterAuthToken> selectAuthTokenIpAndStatus(String ip, String status) throws DataAccessException {
		// TODO Auto-generated method stub
		Map<String,String> param = new HashMap();
		param.put("ip", ip);
		param.put("status", status);
		return sqlSessionTemplate.selectList("sql.resources.twitterloginInfodao.selectAuthTokenIpAndStatus", param)
	}




		
	
}

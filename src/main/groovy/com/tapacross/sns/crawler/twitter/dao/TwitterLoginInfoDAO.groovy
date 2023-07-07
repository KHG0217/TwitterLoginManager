package com.tapacross.sns.crawler.twitter.dao

import javax.annotation.Resource

import org.mybatis.spring.SqlSessionTemplate
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Repository

import com.tapacross.sns.entity.TBTwitterAuthToken

interface loginInfoDAO {
	def String selectExistTwitterId(String email) throws DataAccessException
	def void insertTwitterNewId (TBTwitterAuthToken entity) throws DataAccessException
	def List<TBTwitterAuthToken> selectStatusFTwitterIdList () throws DataAccessException
	def void updateTwitterAuthData (TBTwitterAuthToken entity) throws DataAccessException
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


		
	
}

package com.tapacross.sns.crawler.twitter

import org.springframework.beans.factory.annotation.Autowired

import groovy.transform.TypeChecked

@TypeChecked
class ApplicationProperty { 
	
	@Autowired
	private Properties applicationProperties
	
	public String get(String key) {
		return applicationProperties.getProperty(key)
	}
}

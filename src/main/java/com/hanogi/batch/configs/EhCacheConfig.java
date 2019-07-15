package com.hanogi.batch.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author abhishek.gupta02
 */

@Configuration
public class EhCacheConfig {

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cacheManager = new EhCacheManagerFactoryBean();
		cacheManager.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cacheManager.setShared(true);
		return cacheManager;
	}
}

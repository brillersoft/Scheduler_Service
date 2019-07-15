package com.hanogi.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hanogi.batch.dto.ExecutionStatus;
import com.hanogi.batch.reader.IEmailReader;
import com.hanogi.batch.repositories.ExecutionStatusRepositry;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.services.impl.CacheServiceImpl;
import com.hanogi.batch.utils.bo.EmailMessageData;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApplicationScopeConfig {

	@Autowired
	private CacheServiceImpl cacheServiceImpl;

	@Autowired
	private ApplicationScopeDataLoader applicationScopeDataLoader;

	@Autowired
	ExecutionStatusRepositry executionStatusRepo;

	@Bean("batchProcessorThreadPool")
	public ExecutorService fixedThreadPool() {
		return Executors.newFixedThreadPool(10);
	}

	@Bean("toneProcessorThreadPool")
	public Executor getAsyncExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(7);
		executor.setMaxPoolSize(42);
	//	executor.setQueueCapacity(10);
		executor.setThreadNamePrefix("toneProcessorThreadPool-");
		executor.initialize();
		return executor;
	}

	@Bean("emailDataQueue")
	public BlockingQueue<EmailMessageData> getEmailDataQueue() {
		return new LinkedBlockingQueue<EmailMessageData>();
	}
	
	
	@Bean("cacheErrorMap")
	public Map<String,String> getMapForCacheErrors() {
		return new ConcurrentHashMap<String,String>();
	}

	/*
	 * @Bean public IDataReader dataReader() { return new DataReader(); }
	 */

	@Bean("mailReadersMaps")
	public Map<String, Map<String, IEmailReader>> getMailReaderMap() {

		Map<String, Map<String, IEmailReader>> mailReadersMap = new HashMap<>();

		applicationScopeDataLoader.loadMailReadersMap(mailReadersMap);

		return mailReadersMap;
	}

	@Bean("cacheService")
	public ICacheService cacheService() {
		return cacheServiceImpl;
	}

	@Bean("executionStatusMap")
	public List<ExecutionStatus> executionStatusMap() {
		return executionStatusRepo.getExecutionStatus();
	}

}
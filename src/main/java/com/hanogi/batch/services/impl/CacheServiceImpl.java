package com.hanogi.batch.services.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.constants.ErrorCodes;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.services.ICacheService;
import com.hanogi.batch.utils.bo.EmailMessageData;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author abhishek.gupta02
 *
 */

@Component
public class CacheServiceImpl implements ICacheService, Serializable {

	int noOfElementsInCache = 0;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("cacheErrorMap")
	private Map<String,String> cacheErrorMap;


	private static CacheManager cacheManager = null;

	@PostConstruct
	public void getCacheManagerInstance() {
		cacheManager = CacheManager.newInstance();
		if (cacheManager != null) {
			logger.info("Cache manager has bean created....");
		} else {
			// TO-DO
		}
	}

	/*
	 * validate is messageId exists in cache or not. If exists then update its
	 * status in cache. Else add new messageId in Cache
	 */
	@Override
	public Cache getCacheInstance(CacheType cacheType) {
		Cache cache = null;
		if (cacheType.equals(CacheType.ConcurrentCache)) {
			cache = cacheManager.getCache("concurrentBatchScheduleCache");
		}
		if (cacheType.equals(CacheType.HistroicalCache)) {
			cache = cacheManager.getCache("concurrentBatchScheduleCache");
		}
		return cache;
	}

	@Override
	public boolean checkAddUpdateCache(String messageId, ExecutionStatusEnum executionStatus, Cache cacheInstance){

		logger.info("Caching in progress  for messageId:" + messageId + "...");

		boolean exitsInCache = false;

		try {

			synchronized (this) {

				Element messageIdElement = cacheInstance.get(messageId);

				if (null != messageIdElement) {

					ExecutionStatusEnum messageStatus = (ExecutionStatusEnum) messageIdElement.getObjectValue();

					if (!ExecutionStatusEnum.Complete.equals(messageStatus)) {

						logger.info("MesaageId:" + messageId + "exists in cache.Updating its status.");
						cacheInstance.put(new Element(messageId, executionStatus));
						exitsInCache = true;
					}

				} else {

					logger.info("Updation in cache with adding new element messageId:" + messageId);
					Element element = new Element(messageId, executionStatus);
					cacheInstance.put(element);
				}
			}

		} catch (Exception e) {

			logger.error("Error while updating the message in cache with Error:" + e.getMessage());

			cacheErrorMap.put(messageId, executionStatus.name());
		}

		return exitsInCache;
	}
	

	@Override
	public void cleanConcurrentCache() {
		Cache concurrentCache = cacheManager.getCache("concurrentBatchScheduleCache");

		concurrentCache.removeAll();

		noOfElementsInCache = 0;
	}

	@Override
	public ExecutionStatusEnum getStatusOfMessage(String messageId) {
		Cache concurrentCache = cacheManager.getCache("concurrentBatchScheduleCache");

		if (concurrentCache.isKeyInCache(messageId)) {
			Element element = concurrentCache.get(messageId);

			ExecutionStatusEnum status = (ExecutionStatusEnum) element.getObjectValue();

			return status;
		} else {
			return null;
		}

	}
	
	
	public void updateCacheStatus(List<EmailMessageData> listEmailMessageData,
						ExecutionStatusEnum status, Cache cacheInstance)  {

		for (EmailMessageData emailMessageData : listEmailMessageData) {
			String messageId = null;

			messageId = emailMessageData.getEmailMetaData().getEmailHeader().getMessageId();

			checkAddUpdateCache(messageId, status, cacheInstance);

		}
		
	}

}

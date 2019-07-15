package com.hanogi.batch.services;

import java.util.List;

import com.hanogi.batch.constants.CacheType;
import com.hanogi.batch.constants.ExecutionStatusEnum;
import com.hanogi.batch.exceptions.BrillerBatchDataException;
import com.hanogi.batch.utils.bo.EmailMessageData;

import net.sf.ehcache.Cache;

public interface ICacheService {

	boolean checkAddUpdateCache(String messageId, ExecutionStatusEnum inprogress,Cache cache) throws BrillerBatchDataException;

	void cleanConcurrentCache();
	
	ExecutionStatusEnum getStatusOfMessage(String messageId);
	
	Cache getCacheInstance(CacheType cacheType);

	void updateCacheStatus(List<EmailMessageData> listEmailMessageData, ExecutionStatusEnum failure, Cache cacheInstance);

	

}

package com.hanogi.batch.reader;

import java.util.List;

import net.sf.ehcache.Cache;

public interface ToneReader<T> {

	public void readTone(List<T> data,Cache cache);

}

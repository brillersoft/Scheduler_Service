package com.hanogi.batch.services;

import java.util.Map;
import java.util.Set;

import com.hanogi.batch.dto.EmailHeader;

public interface IToneCalcultionService {

	public void executeToneCalculation(Map<String, Set<EmailHeader>> mailThreadsMap);

}

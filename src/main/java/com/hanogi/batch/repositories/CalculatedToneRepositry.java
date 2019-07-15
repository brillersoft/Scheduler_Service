package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.hanogi.batch.dto.CalculatedTone;

public interface CalculatedToneRepositry extends CrudRepository<CalculatedTone, Integer> {

	public static final String UPDATE_CALCULATED_TONE_QUERY = 
			"UPDATE calculated_tone  set calculated_tone_score = ?1 , benchmark_tone_score = ?2, mail_csat = ?3 where message_id = ?4 ";
	
	public static final String UPDATE_CALCULATED_TONE_INDEX_QUERY = 
			"UPDATE calculated_tone  set mail_index = ?1 where message_id = ?2 ";

	@Transactional
	@Modifying
	@Query(value = UPDATE_CALCULATED_TONE_INDEX_QUERY, nativeQuery = true)
	void updateMailThreadIndex(Double mailThreadIndex, String messageId);
	
	@Transactional
	@Modifying
	@Query(value = UPDATE_CALCULATED_TONE_QUERY, nativeQuery = true)
	void updateToneDetails(double calculatedToneScore,double baseToneScore, double mailCSAT,String messageId);
	
	
	public List<CalculatedTone> findByMailThreadNameIn(List<String> emailThreadList);
	
	public List<CalculatedTone> findByMessageIdIn(List<String> lstMessageId);

}

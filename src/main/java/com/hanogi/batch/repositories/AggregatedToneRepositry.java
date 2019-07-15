package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hanogi.batch.dto.AggregatedTone;

@Repository
public interface AggregatedToneRepositry extends CrudRepository<AggregatedTone, java.lang.Integer> {
	
	
	
	public static final String UPDATE_AGGREGATED_TONE_QUERY = 
			"UPDATE aggregated_tone  set aggregated_tone_score = ?1 , benchmark_tone_score = ?2, thread_csat_score = ?3 where mail_thread_name = ?4 ";
	
	public static final String SELECT_AGGREGATED_TONE_QUERY = 
			"SELECT *  FROM aggregated_tone where mail_thread_name = ?1 ";
	

	@Transactional
	@Modifying
	@Query(value = UPDATE_AGGREGATED_TONE_QUERY, nativeQuery = true)
	void updateToneDetails(double calcToneScore, double calcBaseScore, double mailCSATScore, String threadName);


	@Query(value = SELECT_AGGREGATED_TONE_QUERY, nativeQuery = true)
	public AggregatedTone findByMailThreadName(String mailThreadName);


	List<AggregatedTone> findByMailThreadNameIn(List<String> lstMailThreadNames);

}

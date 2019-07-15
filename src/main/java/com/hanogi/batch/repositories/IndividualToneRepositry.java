package com.hanogi.batch.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hanogi.batch.dto.IndividualTone;

@Repository
public interface IndividualToneRepositry extends CrudRepository<IndividualTone, Integer> {
	
	
	public static final String UPDATE_INDIVIDUAL_TONE_QUERY = 
			"UPDATE individual_tone set mail_index = ?1 where message_id = ?2 ";

	@Transactional
	@Modifying
	@Query(value = UPDATE_INDIVIDUAL_TONE_QUERY, nativeQuery = true)
	void updateMailThreadIndex(Double mailThreadIndex, String messageId);

}

package com.hanogi.batch.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hanogi.batch.dto.EmailHeader;

@Repository
public interface EmailHeaderRepositry extends CrudRepository<EmailHeader, Integer> {
	

	public static final String UPDATE_EMAIL_HEADER_QUERY 
					= "UPDATE email_header set mail_thread_index = ?1 where message_id = ?2 ";
	
	public static final String FETCH_MAIL_DETAILS_BY_MAIL_DATE 
								= "select * from email_header where email_date >= ?1 order by email_date desc ";
	
	@Transactional
	@Modifying
	@Query(value = UPDATE_EMAIL_HEADER_QUERY, nativeQuery = true)
	void updateMailThreadIndex(double mailThreadIndex, String messageId);
	

	public List<EmailHeader> findByMailThreadNameIn(List<String> emailThreadList);


	@Query(value = FETCH_MAIL_DETAILS_BY_MAIL_DATE, nativeQuery = true)
	List<EmailHeader> fetchMailDetails(Date fromDate);
	

}

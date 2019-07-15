package com.hanogi.batch.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.hanogi.batch.dto.RetailAccountCsatSummary;

public interface RetailAccountCsatSummaryRepository extends CrudRepository<RetailAccountCsatSummary, Integer> {
	
	public static final String DELETE_ACCOUNT_CSAT_SUMMARY_QUERY = 
			"delete from retail_account_csat_summary where mail_date = ?1 ";

	
	@Transactional
	@Modifying
	@Query(value = DELETE_ACCOUNT_CSAT_SUMMARY_QUERY, nativeQuery = true)
	void deleteAccountCsatSummaryForDate(Date fromDate);

	

}

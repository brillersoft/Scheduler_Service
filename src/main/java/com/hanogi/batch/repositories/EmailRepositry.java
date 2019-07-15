package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.hanogi.batch.dto.Email;

public interface EmailRepositry extends CrudRepository<Email, String> {
	
	public List<Email> findByEmailIdIn(List<String> emailIdList);

	public static final String EMAIL_IN_Query = "SELECT * from EMAILID where EMAILID.email_id IN (?1) ";

	@Query(value = EMAIL_IN_Query, nativeQuery = true)
	List<Email> getEmailIdIn(String emailIdList);
}

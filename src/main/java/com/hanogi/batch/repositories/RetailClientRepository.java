package com.hanogi.batch.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.RetailClient;

@Repository
public interface RetailClientRepository extends CrudRepository<RetailClient, Integer> {

	List<RetailClient> findByDomainName(String domainName);

}

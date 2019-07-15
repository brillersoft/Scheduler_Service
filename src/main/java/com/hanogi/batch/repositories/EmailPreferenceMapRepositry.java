package com.hanogi.batch.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.EmailPreferenceMap;

@Repository
public interface EmailPreferenceMapRepositry extends CrudRepository<EmailPreferenceMap, Integer> {

}

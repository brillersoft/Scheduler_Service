package com.hanogi.batch.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.EmailMetaDataDto;

@Repository
public interface EmailMetadataRepositry extends CrudRepository<EmailMetaDataDto, Integer> {

}

package com.hanogi.batch.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hanogi.batch.dto.EmailMessageCopy;

@Repository
public interface EmailMessageCopyRepositry extends CrudRepository<EmailMessageCopy, Integer> {

}

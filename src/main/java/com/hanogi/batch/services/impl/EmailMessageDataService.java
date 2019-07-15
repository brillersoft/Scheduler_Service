package com.hanogi.batch.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hanogi.batch.services.IEmailMessageDataService;

/**
 * @author abhishek.gupta02
 *
 */

@Service
public class EmailMessageDataService implements IEmailMessageDataService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

/*	@Autowired
	private EmailMessageDataRepositry emailMessageDataRepo;

	@Override
	@Transactional
	public Boolean insertMessageToDB(EmailMessageData emailMessageData) {
		try {
			return (emailMessageDataRepo.save(emailMessageData) != null);
		} catch (Exception e) {
			logger.error("Error while inserting mesaage data infomation in DB with error message:" + e.getMessage());
			return false;
		}
	}*/
}

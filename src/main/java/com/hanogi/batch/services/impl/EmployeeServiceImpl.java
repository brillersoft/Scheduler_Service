package com.hanogi.batch.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanogi.batch.dto.Email;
import com.hanogi.batch.repositories.EmailRepositry;
import com.hanogi.batch.repositories.EmployeeEmailMappingRepositry;
import com.hanogi.batch.services.EmployeeService;

@Component
public class EmployeeServiceImpl implements EmployeeService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmployeeEmailMappingRepositry empEmailMappingRepo;

	@Autowired
	private EmailRepositry emailRepo;

	public List<Email> getEmployeeMailListToToneAnalyse() {
		logger.info("Getting employee's list to analysis there mail tone.");
		List<Email> emailList = null;

		try {
			List<String> emailIdList = empEmailMappingRepo.findByAnalyseTone("Y").stream()
					.map(emp -> emp.getEmpEmailMapId().getEmailId()).collect(Collectors.toList());

			emailList = emailRepo.findByEmailIdIn(emailIdList);
			return emailList;

		} catch (Exception e) {
			logger.error("Error while getting employee's list for tone analysis with Error:" + e.getMessage());
			return null;
		}
	}
}

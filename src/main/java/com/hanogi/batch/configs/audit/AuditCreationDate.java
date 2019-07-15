package com.hanogi.batch.configs.audit;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditCreationDate<U> {

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdOn;

}

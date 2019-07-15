package com.hanogi.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email_message_copy")
public class EmailMessageCopy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "msg_Id")
	private Integer msgId;

	@Column(name = "msg_json")
	private String msgJson;

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public String getMsgJson() {
		return msgJson;
	}

	public void setMsgJson(String msgJson) {
		this.msgJson = msgJson;
	}

}

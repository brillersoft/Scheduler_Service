package com.hanogi.batch.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author abhishek.gupta02
 *
 */
public class ConnectionException extends Exception {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ConnectionException(String message) {
		super(message);
		logger.error("connetion param missing to connect with mail server.");
	}
}

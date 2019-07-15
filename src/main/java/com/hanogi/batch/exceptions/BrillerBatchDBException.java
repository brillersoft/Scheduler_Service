package com.hanogi.batch.exceptions;

import com.hanogi.batch.constants.ErrorCodes;

/**
 * Custom Exception class to represent any exception encountered while
 * persisting or retrieving data from the source of data
 * @author mayank.agarwal
 *
 */
@SuppressWarnings("serial")
public class BrillerBatchDBException extends Exception {
	
	private ErrorCodes errorCode;

	public BrillerBatchDBException(String message) {
		super(message);

	}
	
	public BrillerBatchDBException(String message,ErrorCodes errorCode) {
		super(message);
		this.errorCode=errorCode;

	}

	public BrillerBatchDBException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BrillerBatchDBException(String message, Throwable cause,ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode=errorCode;
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}
}

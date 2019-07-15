package com.hanogi.batch.exceptions;

import com.hanogi.batch.constants.ErrorCodes;

/**
 * Custom Exception class to represent any connection exception
 * @author mayank.agarwal
 *
 */
@SuppressWarnings("serial")
public class BrillerBatchConnectionException extends Exception {
	
	private ErrorCodes errorCode;

	public BrillerBatchConnectionException(String message) {
		super(message);

	}
	
	public BrillerBatchConnectionException(String message,ErrorCodes errorCode) {
		super(message);
		this.errorCode=errorCode;

	}

	public BrillerBatchConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BrillerBatchConnectionException(String message, Throwable cause,ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode=errorCode;
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}
}

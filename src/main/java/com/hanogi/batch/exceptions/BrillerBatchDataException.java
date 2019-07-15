package com.hanogi.batch.exceptions;

import com.hanogi.batch.constants.ErrorCodes;

/**
 * Custom Exception class to represent any exception encountered while
 * extracting data from the source of data
 * @author mayank.agarwal
 *
 */
@SuppressWarnings("serial")
public class BrillerBatchDataException extends Exception {
	
	private ErrorCodes errorCode;

	public BrillerBatchDataException(String message) {
		super(message);

	}
	
	public BrillerBatchDataException(String message,ErrorCodes errorCode) {
		super(message);
		this.errorCode=errorCode;

	}

	public BrillerBatchDataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BrillerBatchDataException(String message, Throwable cause,ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode=errorCode;
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}
}

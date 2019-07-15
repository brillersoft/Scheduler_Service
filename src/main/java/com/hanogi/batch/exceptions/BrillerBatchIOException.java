package com.hanogi.batch.exceptions;

/**
 * Custom Exception class to represent any connection exception
 * @author mayank.agarwal
 *
 */
@SuppressWarnings("serial")
public class BrillerBatchIOException extends Exception {

	public BrillerBatchIOException(String message) {
		super(message);

	}

	public BrillerBatchIOException(String message, Throwable cause) {
		super(message, cause);
	}
}

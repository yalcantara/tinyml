package com.tinyml.structs;

public class IllegalDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4892640081115872029L;

	public IllegalDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public IllegalDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalDataException(String message) {
		super(message);
	}

	public IllegalDataException(Throwable cause) {
		super(cause);
	}

}

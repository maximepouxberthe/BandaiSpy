package com.skampe.utils.exceptions;

public class TypeMismatchException extends Exception {

	private static final long serialVersionUID = 20131021L;

	private final String code;

	public TypeMismatchException() {
		this.code = null;
	}

	public TypeMismatchException(final String message) {
		this(message, (String) null);
	}

	public TypeMismatchException(final Throwable cause) {
		this(cause, null);
	}

	public TypeMismatchException(final String message, final Throwable cause) {
		this(message, cause, null);
	}

	public TypeMismatchException(final String message, final String code) {
		super(message);
		this.code = code;
	}

	public TypeMismatchException(final Throwable cause, final String code) {
		super(cause);
		this.code = code;
	}

	public TypeMismatchException(final String message, final Throwable cause, final String code) {
		super(message, cause);
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
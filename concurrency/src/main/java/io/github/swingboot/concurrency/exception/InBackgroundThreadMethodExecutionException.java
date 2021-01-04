package io.github.swingboot.concurrency.exception;

public class InBackgroundThreadMethodExecutionException extends Exception {
	private static final long serialVersionUID = 1780100580186553744L;

	public InBackgroundThreadMethodExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InBackgroundThreadMethodExecutionException(String message) {
		super(message);
	}

}

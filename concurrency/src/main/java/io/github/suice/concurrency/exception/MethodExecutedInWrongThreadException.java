package io.github.suice.concurrency.exception;

public class MethodExecutedInWrongThreadException extends RuntimeException {

	private static final long serialVersionUID = 5208734274016003414L;

	public MethodExecutedInWrongThreadException(String message) {
		super(message);
	}

}

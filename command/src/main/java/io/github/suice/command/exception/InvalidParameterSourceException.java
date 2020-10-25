package io.github.suice.command.exception;

public class InvalidParameterSourceException extends RuntimeException {
	private static final long serialVersionUID = -6341310891431390434L;

	public InvalidParameterSourceException() {
		super();
	}

	public InvalidParameterSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParameterSourceException(String message) {
		super(message);
	}

	public InvalidParameterSourceException(Throwable cause) {
		super(cause);
	}

}

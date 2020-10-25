package io.github.suice.command.exception;

public class InvalidCommandDeclarationException extends RuntimeException {
	private static final long serialVersionUID = -7712534813204714622L;

	public InvalidCommandDeclarationException() {
		super();
	}

	public InvalidCommandDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCommandDeclarationException(String message) {
		super(message);
	}

	public InvalidCommandDeclarationException(Throwable cause) {
		super(cause);
	}

}

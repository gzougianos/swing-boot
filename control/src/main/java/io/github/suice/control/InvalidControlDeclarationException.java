package io.github.suice.control;

public class InvalidControlDeclarationException extends RuntimeException {
	private static final long serialVersionUID = -7712534813204714622L;

	public InvalidControlDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidControlDeclarationException(String message) {
		super(message);
	}

}

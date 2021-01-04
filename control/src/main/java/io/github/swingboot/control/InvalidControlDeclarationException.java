package io.github.swingboot.control;

public class InvalidControlDeclarationException extends RuntimeException {
	private static final long serialVersionUID = -7712534813204714622L;

	InvalidControlDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	InvalidControlDeclarationException(String message) {
		super(message);
	}

}

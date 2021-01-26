package io.github.swingboot.control;

public class InvalidParameterSourceDeclarationException extends RuntimeException {
	private static final long serialVersionUID = -7712534813244714622L;

	InvalidParameterSourceDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	InvalidParameterSourceDeclarationException(String message) {
		super(message);
	}

}

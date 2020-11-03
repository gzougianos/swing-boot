package io.github.suice.command;

public class CommandDeclarationException extends RuntimeException {
	private static final long serialVersionUID = -7712534813204714622L;

	public CommandDeclarationException() {
		super();
	}

	public CommandDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandDeclarationException(String message) {
		super(message);
	}

	public CommandDeclarationException(Throwable cause) {
		super(cause);
	}

}

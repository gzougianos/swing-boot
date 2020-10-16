package io.github.suice.command;

public class CommandInitializationException extends RuntimeException {
	private static final long serialVersionUID = -83603167847598894L;

	public CommandInitializationException() {
		super();
	}

	public CommandInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandInitializationException(String message) {
		super(message);
	}

	public CommandInitializationException(Throwable cause) {
		super(cause);
	}

}

package io.github.suice.command;

public class CommandInstallationException extends RuntimeException {
	private static final long serialVersionUID = -83603167847598894L;

	public CommandInstallationException() {
		super();
	}

	public CommandInstallationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandInstallationException(String message) {
		super(message);
	}

	public CommandInstallationException(Throwable cause) {
		super(cause);
	}

}

package io.github.suice.control.parameter;

public class ParameterSourceException extends RuntimeException {
	private static final long serialVersionUID = 7743144966139289046L;

	public ParameterSourceException() {
		super();
	}

	public ParameterSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParameterSourceException(String message) {
		super(message);
	}

	public ParameterSourceException(Throwable cause) {
		super(cause);
	}

}

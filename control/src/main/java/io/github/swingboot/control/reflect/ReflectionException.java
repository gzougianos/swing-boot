package io.github.swingboot.control.reflect;

public class ReflectionException extends RuntimeException {
	private static final long serialVersionUID = 2112303130641627516L;

	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReflectionException(Throwable cause) {
		super(cause);
	}
}

package io.github.swingboot.control.installation.annotation;

import javax.swing.JFrame;

public enum WindowState {
	//@formatter:off
	NORMAL(JFrame.NORMAL),
	ICONIFIED(JFrame.ICONIFIED),
	MAXIMIZED_VERT(JFrame.MAXIMIZED_VERT),
	MAXIMIZED_HORIZ(JFrame.MAXIMIZED_HORIZ),
	MAXIMIZED_BOTH(JFrame.MAXIMIZED_BOTH),
	ANY(-5000);
	//@formatter:on

	private final int swingValue;

	private WindowState(int swingValue) {
		this.swingValue = swingValue;
	}

	public boolean matches(int swingValue) {
		if (this == ANY)
			return true;

		return this.swingValue == swingValue;
	}
}

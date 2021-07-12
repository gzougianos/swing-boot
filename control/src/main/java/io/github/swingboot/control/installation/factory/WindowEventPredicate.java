package io.github.swingboot.control.installation.factory;

import java.awt.event.WindowEvent;

import io.github.swingboot.control.installation.annotation.WindowState;

public class WindowEventPredicate {

	private final WindowState declaredOldState, declaredNewState;

	public WindowEventPredicate(WindowState oldStateInAnnotation, WindowState newStateInAnnotation) {
		this.declaredOldState = oldStateInAnnotation;
		this.declaredNewState = newStateInAnnotation;
	}

	boolean test(WindowEvent event) {
		boolean oldStateMatches = declaredOldState.matches(event.getOldState());
		boolean newStateMatches = declaredNewState.matches(event.getNewState());
		return oldStateMatches && newStateMatches;
	}

}

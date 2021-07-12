package io.github.swingboot.control.installation.factory;

import java.awt.event.MouseEvent;

public class MouseEventPredicate {
	public static final int ANY_BUTTON = -999999;
	public static final int ANY_CLICK_COUNT = -999999;
	public static final int ANY_MODIFIER = -999999;

	private final int declaredButton;
	private final int declaredClickCount;
	private final int declaredModifiers;

	MouseEventPredicate(int buttonInAnnotation, int clickCountInAnnotation, int modifiersInAnnotation) {
		this.declaredButton = buttonInAnnotation;
		this.declaredClickCount = clickCountInAnnotation;
		this.declaredModifiers = modifiersInAnnotation;
	}

	boolean test(MouseEvent event) {
		final boolean anyButton = declaredButton == ANY_BUTTON;
		final boolean anyModifier = declaredModifiers == ANY_MODIFIER;
		final boolean anyClickCount = declaredClickCount == ANY_CLICK_COUNT;

		boolean modifiersMatch = anyModifier || maskMatch(event);
		boolean buttonMatch = anyButton || declaredButton == event.getButton();
		boolean clickCountMatch = anyClickCount || declaredClickCount == event.getClickCount();

		return modifiersMatch && buttonMatch && clickCountMatch;
	}

	private boolean maskMatch(MouseEvent event) {
		return (event.getModifiers() & declaredModifiers) == declaredModifiers;
	}

}

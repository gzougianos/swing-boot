package io.github.swingboot.control.annotation;

import java.util.function.Predicate;

public enum BooleanValue {
	//@formatter:off
	TRUE(b -> b.equals(Boolean.TRUE)),
	FALSE(b -> b.equals(Boolean.FALSE)),
	ANY(b-> true);
	//@formatter:on

	private final Predicate<Boolean> matcher;

	private BooleanValue(Predicate<Boolean> matcher) {
		this.matcher = matcher;
	}

	public boolean matches(Boolean b) {
		return matcher.test(b);
	}
}

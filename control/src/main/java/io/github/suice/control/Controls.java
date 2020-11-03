package io.github.suice.control;

public interface Controls {
	<T extends Control<?>> void perform(Class<T> controlType);

	<S, T extends Control<S>> void perform(Class<T> controlType, S parameters);
}

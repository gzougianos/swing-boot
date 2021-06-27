package io.github.swingboot.control.binding;

import javax.inject.Inject;

import com.google.inject.Injector;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;

class GuiceInjectableControls implements Controls {
	private Injector injector;

	@Inject
	GuiceInjectableControls(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T extends Control<?>> void perform(Class<T> controlType) {
		Control<?> controlInstance = injector.getInstance(controlType);
		controlInstance.perform(null);
	}

	@Override
	public <S, T extends Control<S>> void perform(Class<T> controlType, S parameter) {
		Control<S> controlInstance = injector.getInstance(controlType);
		controlInstance.perform(parameter);
	}

}

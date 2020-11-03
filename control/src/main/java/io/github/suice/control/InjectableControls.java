package io.github.suice.control;

import javax.inject.Inject;

import com.google.inject.Injector;

public class InjectableControls implements Controls {
	private Injector injector;

	@Inject
	public InjectableControls(Injector injector) {
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

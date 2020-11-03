package io.github.suice.control;

import static io.github.suice.control.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.control.parameter.ParameterSource;

public class ControlDeclarationPerformer {
	private static final Logger log = LoggerFactory.getLogger(ControlDeclarationPerformer.class);
	private Controls controls;
	private ObjectOwnedControlDeclaration controlDeclaration;

	public ControlDeclarationPerformer(Controls controls, ObjectOwnedControlDeclaration controlDeclaration) {
		this.controls = controls;
		this.controlDeclaration = controlDeclaration;
	}

	public void perform(AWTEvent event) {
		Class<?> parType = controlDeclaration.getControlParameterType();

		if (controlDeclaration.getParameterSource().isPresent()) {
			executeInvokingSource(controlDeclaration.getParameterSource().get(), event);
		} else if (equalsOrExtends(event.getClass(), parType)) {
			executeInjectingAwtEvent(event);
		} else {
			controls.perform(controlDeclaration.getControlType());
		}
	}

	private void executeInvokingSource(ParameterSource parameterSource, AWTEvent event) {
		Object parameterSourceValue = parameterSource.getValue(controlDeclaration.getOwner(), event);
		Class<? extends Control<?>> controlType = controlDeclaration.getControlType();

		try {
			controls.getClass().getMethod("perform", Class.class, Object.class).invoke(controls, controlType,
					parameterSourceValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			log.error("Error performing control type " + controlType + " with parameter source " + parameterSource + ".", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void executeInjectingAwtEvent(AWTEvent event) {
		Class<? extends Control<AWTEvent>> controlParameterType = (Class<? extends Control<AWTEvent>>) controlDeclaration
				.getControlType();
		controls.perform(controlParameterType, event);
	}

}

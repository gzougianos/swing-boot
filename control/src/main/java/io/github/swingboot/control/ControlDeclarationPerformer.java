package io.github.swingboot.control;

import java.awt.AWTEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import io.github.swingboot.control.parameter.ParameterSource;
import io.github.swingboot.control.reflect.ReflectionException;

class ControlDeclarationPerformer {
	private Controls controls;
	private ObjectOwnedControlDeclaration controlDeclaration;

	ControlDeclarationPerformer(Controls controls, ObjectOwnedControlDeclaration controlDeclaration) {
		this.controls = controls;
		this.controlDeclaration = controlDeclaration;
	}

	public void perform(AWTEvent event) {
		Optional<ParameterSource> possibleParameterSource = controlDeclaration.getParameterSource();
		if (possibleParameterSource.isPresent()) {
			executeInvokingSource(possibleParameterSource.get(), event);
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
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ReflectionException("Error performing control type " + controlType
					+ " with parameter source " + parameterSource + ".", e);
		}
	}

}

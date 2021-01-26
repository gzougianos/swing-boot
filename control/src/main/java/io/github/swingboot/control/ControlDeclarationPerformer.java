package io.github.swingboot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import io.github.swingboot.control.parameter.ParameterSource;
import io.github.swingboot.control.reflect.ReflectionException;

class ControlDeclarationPerformer {
	private Controls controls;
	private Class<? extends Control<?>> controlType;
	private ParameterSource parameterSource;
	private Object owner;

	ControlDeclarationPerformer(Controls controls, Class<? extends Control<?>> controlType,
			ParameterSource parameterSource, Object owner) {
		super();
		this.controls = controls;
		this.controlType = controlType;
		this.parameterSource = parameterSource;
		this.owner = owner;
	}

	public void perform(EventObject event) {
		if (parameterSource != null) {
			executeInvokingSource(event);
		} else {
			controls.perform(controlType);
		}
	}

	private void executeInvokingSource(EventObject event) {
		Object parameterSourceValue = parameterSource.getValue(owner, event);

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

package io.github.suice.control;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

import io.github.suice.control.parameter.ParameterSource;

public class ObjectOwnedControlDeclaration {

	private Object owner;
	private ControlDeclaration declaration;

	public ObjectOwnedControlDeclaration(Object owner, ControlDeclaration declaration) {
		this.owner = owner;
		this.declaration = declaration;
	}

	public Object getOwner() {
		return owner;
	}

	public Optional<ParameterSource> getParameterSource() {
		return declaration.getParameterSource();
	}

	public AnnotatedElement getTargetElement() {
		return declaration.getTargetElement();
	}

	public String getId() {
		return declaration.getId();
	}

	public Class<?> getControlParameterType() {
		return declaration.getControlTypeInfo().getParameterType();
	}

	public Class<? extends Control<?>> getControlType() {
		return declaration.getControlTypeInfo().getControlType();
	}

	public Annotation getAnnotation() {
		return declaration.getAnnotation();
	}

	public Component getTargetComponent() {
		try {
			if (getTargetElement() instanceof Field) {
				Field targetField = (Field) getTargetElement();
				ensureAccessible(targetField);
				return (Component) targetField.get(getOwner());
			} else {
				return (Component) getOwner();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Error getting value from target element " + getTargetComponent(), e);
		}
	}

	private void ensureAccessible(Field targetField) {
		if (!targetField.isAccessible())
			targetField.setAccessible(true);
	}

	@Override
	public String toString() {
		return "ObjectOwnedControlDeclaration [owner=" + owner + ", declaration=" + declaration + "]";
	}

}

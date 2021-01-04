package io.github.swingboot.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

import io.github.swingboot.control.annotation.installer.AnnotationInstaller;
import io.github.swingboot.control.parameter.ParameterSource;
import io.github.swingboot.control.reflect.ReflectionException;

class ObjectOwnedControlDeclaration {

	private Object owner;
	private ControlDeclaration declaration;

	ObjectOwnedControlDeclaration(Object owner, ControlDeclaration declaration) {
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

	public Class<? extends AnnotationInstaller> getInstallerType() {
		return declaration.getInstallerType();
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

	public Object getTargetObject() {
		AnnotatedElement targetElement = declaration.getTargetElement();
		try {
			if (targetElement instanceof Field) {
				Field targetField = (Field) targetElement;
				ensureAccessible(targetField);
				return targetField.get(getOwner());
			} else {
				return getOwner();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ReflectionException("Error getting value from target element " + targetElement, e);
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

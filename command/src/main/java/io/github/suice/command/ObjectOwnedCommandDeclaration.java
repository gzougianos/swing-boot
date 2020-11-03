package io.github.suice.command;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

import io.github.suice.parameter.ParameterSource;

public class ObjectOwnedCommandDeclaration {

	private Object owner;
	private CommandDeclaration declaration;

	public ObjectOwnedCommandDeclaration(Object owner, CommandDeclaration declaration) {
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

	public Class<?> getCommandGenericParameterType() {
		return declaration.getCommandGenericParameterType();
	}

	public Class<? extends Command<?>> getCommandType() {
		return declaration.getCommandType();
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
			throw new RuntimeException("Error getting value from target element " + getTargetComponent());
		}
	}

	private void ensureAccessible(Field targetField) {
		if (!targetField.isAccessible())
			targetField.setAccessible(true);
	}

	@Override
	public String toString() {
		return "ObjectOwnedCommandDeclaration [owner=" + owner + ", declaration=" + declaration + "]";
	}

}

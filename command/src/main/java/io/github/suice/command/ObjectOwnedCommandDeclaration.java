package io.github.suice.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import io.github.suice.command.reflect.FieldOrMethod;

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

	public Optional<FieldOrMethod> getParameterSource() {
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

	@Override
	public String toString() {
		return "ObjectOwnedCommandDeclaration [owner=" + owner + ", declaration=" + declaration + "]";
	}

}

package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import io.github.suice.command.annotation.DeclaresCommand;
import io.github.suice.command.exception.InvalidCommandDeclarationException;
import io.github.suice.command.reflect.FieldOrMethod;
import io.github.suice.command.reflect.ReflectionUtils;

public class CommandDeclaration {

	private FieldOrMethod parameterSource;
	private String id;
	private Class<? extends Command<?>> commandType;
	private Class<?> commandGenericParameterType;
	private Annotation annotation;
	private AnnotatedElement targetElement;

	@SuppressWarnings("unchecked")
	public CommandDeclaration(Annotation annotation, AnnotatedElement targetElement) {
		this.targetElement = targetElement;
		if (!annotation.annotationType().isAnnotationPresent(DeclaresCommand.class))
			throw new InvalidCommandDeclarationException(annotation.annotationType() + " is not a @DeclaresCommand annotation.");

		checkIfAnnotationCanBeInstalledToTargetElement(annotation, targetElement);

		id = String.valueOf(invokeMethodOfAnnotation(annotation, "id"));
		if ("".equals(id))
			id = annotation.toString();

		commandType = (Class<? extends Command<?>>) invokeMethodOfAnnotation(annotation, "value");

		this.annotation = annotation;
		commandGenericParameterType = ReflectionUtils.getCommandGenericParameterType(commandType);
	}

	private void checkIfAnnotationCanBeInstalledToTargetElement(Annotation annotation, AnnotatedElement targetElement) {
		DeclaresCommand declaresCommand = annotation.annotationType().getAnnotation(DeclaresCommand.class);
		Class<?> targetType = getTargetType(targetElement);

		if (!supportsType(declaresCommand, targetType)) {
			throw new InvalidCommandDeclarationException(
					"@" + annotation.annotationType().getSimpleName() + " cannot be delcared to objects of type " + targetType);
		}
	}

	private Class<?> getTargetType(AnnotatedElement targetElement) {
		if (targetElement instanceof Field)
			return ((Field) targetElement).getType();
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;

		throw new InvalidCommandDeclarationException("Unsupported target element:" + targetElement);
	}

	private boolean supportsType(DeclaresCommand declaresCommand, Class<?> type) {
		for (Class<?> clazz : declaresCommand.value()) {
			if (equalsOrExtends(type, clazz))
				return true;
		}
		return false;
	}

	public Optional<FieldOrMethod> getParameterSource() {
		return Optional.ofNullable(parameterSource);
	}

	public AnnotatedElement getTargetElement() {
		return targetElement;
	}

	public boolean targetsField() {
		return targetElement instanceof Field;
	}

	public String getId() {
		return id;
	}

	public Class<?> getCommandGenericParameterType() {
		return commandGenericParameterType;
	}

	public Class<? extends Command<?>> getCommandType() {
		return commandType;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	void setParameterSource(FieldOrMethod parameterSource) {
		checkIfParameterSourceReturnTypeMatchesCommandGenericType(parameterSource);
		this.parameterSource = parameterSource;
	}

	private void checkIfParameterSourceReturnTypeMatchesCommandGenericType(FieldOrMethod parameterSource) {
		Class<?> parameterSourceReturnType = parameterSource.getValueReturnType();

		if (!equalsOrExtends(parameterSourceReturnType, commandGenericParameterType))
			throw new InvalidCommandDeclarationException("@ParameterSource(" + id + ") in  " + parameterSource.getDeclaringClass()
					+ " can only return " + commandGenericParameterType.getSimpleName() + " values. It currently returns "
					+ parameterSource.getValueReturnType().getSimpleName() + ".");
	}

	private static Object invokeMethodOfAnnotation(Annotation annotation, String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new InvalidCommandDeclarationException("Cannot invoke method " + method + " of annotation " + annotation + ".",
					e);
		}
	}

	@Override
	public String toString() {
		return "CommandDeclaration [parameterSource=" + parameterSource + ", id=" + id + ", commandType=" + commandType
				+ ", commandGenericParameterType=" + commandGenericParameterType + ", annotation=" + annotation
				+ ", targetElement=" + targetElement + "]";
	}

}

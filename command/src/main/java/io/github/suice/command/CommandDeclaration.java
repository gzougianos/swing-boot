package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Optional;

import io.github.suice.command.annotation.DeclaresCommand;
import io.github.suice.command.reflect.ReflectionUtils;
import io.github.suice.parameter.ParameterSource;

public class CommandDeclaration {

	private ParameterSource parameterSource;
	private String id;
	private Class<? extends Command<?>> commandType;
	private Class<?> commandGenericParameterType;
	private Annotation annotation;
	private AnnotatedElement targetElement;
	private String parameterSourceId;

	@SuppressWarnings("unchecked")
	private CommandDeclaration(Annotation annotation, AnnotatedElement targetElement) {
		this.targetElement = targetElement;
		if (!annotation.annotationType().isAnnotationPresent(DeclaresCommand.class))
			throw new CommandDeclarationException(annotation.annotationType() + " is not a @DeclaresCommand annotation.");

		checkIfAnnotationCanBeInstalledToTargetElement(annotation, targetElement);

		id = String.valueOf(invokeMethodOfAnnotation(annotation, "id"));
		if ("".equals(id))
			id = annotation.toString() + targetElement.toString();

		parameterSourceId = String.valueOf(invokeMethodOfAnnotation(annotation, "parameterSource"));

		commandType = (Class<? extends Command<?>>) invokeMethodOfAnnotation(annotation, "value");

		this.annotation = annotation;
		commandGenericParameterType = ReflectionUtils.getCommandGenericParameterType(commandType);
	}

	public CommandDeclaration(Annotation annotation, Class<?> clazz) {
		this(annotation, (AnnotatedElement) clazz);
	}

	public CommandDeclaration(Annotation annotation, Field field) {
		this(annotation, (AnnotatedElement) field);
	}

	private void checkIfAnnotationCanBeInstalledToTargetElement(Annotation annotation, AnnotatedElement targetElement) {
		DeclaresCommand declaresCommand = annotation.annotationType().getAnnotation(DeclaresCommand.class);
		Class<?> targetType = getTargetType(targetElement);

		if (!supportsType(declaresCommand, targetType)) {
			Class<?> declaringClass = getTargetElementDeclaringClass();
			throw new CommandDeclarationException(
					annotation + " declared in " + declaringClass + " cannot be installed to objects of type " + targetType);
		}
	}

	private Class<?> getTargetType(AnnotatedElement targetElement) {
		if (targetElement instanceof Field)
			return ((Field) targetElement).getType();
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;

		throw new CommandDeclarationException("Unsupported target element:" + targetElement);
	}

	private boolean supportsType(DeclaresCommand declaresCommand, Class<?> type) {
		for (Class<?> clazz : declaresCommand.value()) {
			if (equalsOrExtends(type, clazz))
				return true;
		}
		return false;
	}

	public Optional<ParameterSource> getParameterSource() {
		return Optional.ofNullable(parameterSource);
	}

	public AnnotatedElement getTargetElement() {
		return targetElement;
	}

	public String getParameterSourceId() {
		return parameterSourceId;
	}

	public boolean expectsParameterSource() {
		return !parameterSourceId.isEmpty();
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

	void setParameterSource(ParameterSource parameterSource) {
		if (Void.class.equals(commandGenericParameterType))
			throw new CommandDeclarationException(
					this.getAnnotation() + " does not support parameter source. Control's generic parameter is Void.");

		if (!parameterSource.getId().equals(parameterSourceId))
			throw new CommandDeclarationException(
					"Parameter source " + parameterSource + " has different id than command declartion");

		checkIfParameterSourceReturnTypeMatchesCommandGenericType(parameterSource);
		this.parameterSource = parameterSource;
	}

	private void checkIfParameterSourceReturnTypeMatchesCommandGenericType(ParameterSource parameterSource) {
		Class<?> parameterSourceReturnType = parameterSource.getValueReturnType();

		if (!equalsOrExtends(parameterSourceReturnType, commandGenericParameterType)) {
			Class<?> declaringClass = getTargetElementDeclaringClass();
			throw new CommandDeclarationException("@ParameterSource(" + parameterSource.getId() + ") in " + declaringClass
					+ " can only return " + commandGenericParameterType.getSimpleName() + " values. It currently returns "
					+ parameterSource.getValueReturnType().getSimpleName() + ".");
		}
	}

	private Class<?> getTargetElementDeclaringClass() {
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;
		if (targetElement instanceof Member)
			return ((Member) targetElement).getDeclaringClass();

		throw new UnsupportedOperationException("Error finding declaring class of target element: " + targetElement);
	}

	private static Object invokeMethodOfAnnotation(Annotation annotation, String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new CommandDeclarationException("Cannot invoke method " + method + " of annotation " + annotation + ".",
					e);
		}
	}

	@Override
	public String toString() {
		return "CommandDeclaration [parameterSource=" + parameterSource + ", id=" + id + ", commandType=" + commandType
				+ ", commandGenericParameterType=" + commandGenericParameterType + ", annotation=" + annotation
				+ ", targetElement=" + targetElement + ", parameterId=" + parameterSourceId + "]";
	}

}

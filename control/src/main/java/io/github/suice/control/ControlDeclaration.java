package io.github.suice.control;

import static io.github.suice.control.reflect.ReflectionUtils.equalsOrExtends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Optional;

import io.github.suice.control.annotation.DeclaresControl;
import io.github.suice.control.parameter.ParameterSource;

public class ControlDeclaration {

	private ParameterSource parameterSource;
	private String id;
	private ControlTypeInfo controlTypeInfo;
	private Annotation annotation;
	private AnnotatedElement targetElement;
	private String parameterSourceId;

	@SuppressWarnings("unchecked")
	private ControlDeclaration(Annotation annotation, AnnotatedElement targetElement) {
		this.targetElement = targetElement;
		this.annotation = annotation;

		checkIfThisAnnotationIsDeclaredOnThisElement();
		checkIfAnnotationDeclaresControl();
		checkIfAnnotationCanBeInstalledToTargetElement();

		id = String.valueOf(invokeMethodOfAnnotation("id"));
		if ("".equals(id))
			id = annotation.toString() + targetElement.toString();

		parameterSourceId = String.valueOf(invokeMethodOfAnnotation("parameterSource"));

		Class<? extends Control<?>> controlType = (Class<? extends Control<?>>) invokeMethodOfAnnotation("value");
		controlTypeInfo = ControlTypeInfo.of(controlType);

		checkIfParameterSourceGivenWhenNonNullableParameter();
		checkIfParameterSourceGivenWhenControlTakesNoParameter();
	}

	private void checkIfParameterSourceGivenWhenControlTakesNoParameter() {
		if (controlTypeInfo.isParameterless() && !parameterSourceId.isEmpty()) {
			String format = "%s declares a non parameterized control but parameter source was given. Remove parameterSource value.";
			throw new InvalidControlDeclarationException(String.format(format, this.toString()));
		}
	}

	private void checkIfParameterSourceGivenWhenNonNullableParameter() {
		if (controlTypeInfo.isParameterless())
			return;

		if (!controlTypeInfo.isParameterNullable() && parameterSourceId.isEmpty()) {
			String format = "%s declares a parameterized control with non-nullable %s parameter but no parameter source was declared.";
			throw new InvalidControlDeclarationException(
					String.format(format, this.toString(), controlTypeInfo.getParameterType().getSimpleName()));
		}
	}

	private void checkIfAnnotationDeclaresControl() {
		if (!annotation.annotationType().isAnnotationPresent(DeclaresControl.class))
			throw new InvalidControlDeclarationException(annotation.annotationType() + " is not a @DeclaresControl annotation.");
	}

	private void checkIfThisAnnotationIsDeclaredOnThisElement() {
		if (!Arrays.asList(targetElement.getDeclaredAnnotations()).stream().anyMatch(a -> a == annotation))
			throw new InvalidControlDeclarationException("The annotation should be declared to target element.");
	}

	public ControlDeclaration(Annotation annotation, Class<?> clazz) {
		this(annotation, (AnnotatedElement) clazz);
	}

	public ControlDeclaration(Annotation annotation, Field field) {
		this(annotation, (AnnotatedElement) field);
	}

	private void checkIfAnnotationCanBeInstalledToTargetElement() {
		DeclaresControl declaresControl = annotation.annotationType().getAnnotation(DeclaresControl.class);
		Class<?> targetType = getTargetType(targetElement);

		if (!supportsType(declaresControl, targetType)) {
			Class<?> declaringClass = getTargetElementDeclaringClass();
			throw new InvalidControlDeclarationException(
					annotation + " declared in " + declaringClass + " cannot be installed to objects of type " + targetType);
		}
	}

	private Class<?> getTargetType(AnnotatedElement targetElement) {
		if (targetElement instanceof Field)
			return ((Field) targetElement).getType();
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;

		throw new InvalidControlDeclarationException("Unsupported target element:" + targetElement);
	}

	private boolean supportsType(DeclaresControl declaresControl, Class<?> type) {
		for (Class<?> clazz : declaresControl.value()) {
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

	public ControlTypeInfo getControlTypeInfo() {
		return controlTypeInfo;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	void setParameterSource(ParameterSource parameterSource) {
		if (controlTypeInfo.isParameterless())
			throw new InvalidControlDeclarationException(
					this.getAnnotation() + " does not support parameter source. Control's generic parameter is Void.");

		if (!parameterSource.getId().equals(parameterSourceId))
			throw new InvalidControlDeclarationException(
					this + " does not a support a parameter source with id " + parameterSource.getId() + "");

		checkIfParameterSourceReturnTypeMatchesControlParameterType(parameterSource);
		this.parameterSource = parameterSource;
	}

	private void checkIfParameterSourceReturnTypeMatchesControlParameterType(ParameterSource parameterSource) {
		Class<?> parameterSourceReturnType = parameterSource.getValueReturnType();

		if (!equalsOrExtends(parameterSourceReturnType, controlTypeInfo.getParameterType())) {
			throw new InvalidControlDeclarationException(this + " declares a parameter source that returns "
					+ parameterSourceReturnType.getSimpleName() + " values while control takes "
					+ controlTypeInfo.getParameterType().getSimpleName() + " parameter.");
		}
	}

	private Class<?> getTargetElementDeclaringClass() {
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;
		if (targetElement instanceof Member)
			return ((Member) targetElement).getDeclaringClass();

		throw new UnsupportedOperationException("Error finding declaring class of target element: " + targetElement);
	}

	private Object invokeMethodOfAnnotation(String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new InvalidControlDeclarationException("Cannot invoke method " + method + " of annotation " + annotation + ".", e);
		}
	}

	@Override
	public String toString() {
		final String format = "ControlDeclaration @%s(id=%s, parameterSource=%s, control=%s.class) on %s";
		final boolean isAutoGeneratedId = id.startsWith(annotation.toString());
		final String clazzName = getTargetElementDeclaringClass().getSimpleName();
		//@formatter:off
		return String.format(format,
				annotation.annotationType().getSimpleName(),
				isAutoGeneratedId ? "<AUTO_GENERATED>" : id,
				parameterSourceId.isEmpty() ? "<NONE>" : parameterSourceId,
				controlTypeInfo.getControlType().getSimpleName(),
				targetElement instanceof Field ? "field '"+ ((Field)targetElement).getName() + "' of class " + clazzName : clazzName );
		//@formatter:on
	}

}

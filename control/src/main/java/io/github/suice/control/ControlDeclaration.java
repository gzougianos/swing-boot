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
import io.github.suice.control.reflect.ReflectionUtils;

public class ControlDeclaration {

	private ParameterSource parameterSource;
	private String id;
	private Class<? extends Control<?>> controlType;
	private Class<?> controlParameterType;
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

		controlType = (Class<? extends Control<?>>) invokeMethodOfAnnotation("value");

		controlParameterType = ReflectionUtils.getControlParameterType(controlType);
	}

	private void checkIfAnnotationDeclaresControl() {
		if (!annotation.annotationType().isAnnotationPresent(DeclaresControl.class))
			throw new ControlDeclarationException(annotation.annotationType() + " is not a @DeclaresControl annotation.");
	}

	private void checkIfThisAnnotationIsDeclaredOnThisElement() {
		if (!Arrays.asList(targetElement.getDeclaredAnnotations()).stream().anyMatch(a -> a == annotation))
			throw new ControlDeclarationException("The annotation should be declared to target element.");
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
			throw new ControlDeclarationException(
					annotation + " declared in " + declaringClass + " cannot be installed to objects of type " + targetType);
		}
	}

	private Class<?> getTargetType(AnnotatedElement targetElement) {
		if (targetElement instanceof Field)
			return ((Field) targetElement).getType();
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;

		throw new ControlDeclarationException("Unsupported target element:" + targetElement);
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

	public Class<?> getControlParameterType() {
		return controlParameterType;
	}

	public Class<? extends Control<?>> getControlType() {
		return controlType;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	void setParameterSource(ParameterSource parameterSource) {
		if (Void.class.equals(controlParameterType))
			throw new ControlDeclarationException(
					this.getAnnotation() + " does not support parameter source. Control's generic parameter is Void.");

		if (!parameterSource.getId().equals(parameterSourceId))
			throw new ControlDeclarationException(
					"Parameter source " + parameterSource + " has different id than control declartion.");

		checkIfParameterSourceReturnTypeMatchesControlParameterType(parameterSource);
		this.parameterSource = parameterSource;
	}

	private void checkIfParameterSourceReturnTypeMatchesControlParameterType(ParameterSource parameterSource) {
		Class<?> parameterSourceReturnType = parameterSource.getValueReturnType();

		if (!equalsOrExtends(parameterSourceReturnType, controlParameterType)) {
			Class<?> declaringClass = getTargetElementDeclaringClass();
			throw new ControlDeclarationException("@ParameterSource(" + parameterSource.getId() + ") in " + declaringClass
					+ " can only return " + controlParameterType.getSimpleName() + " values. It currently returns "
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

	private Object invokeMethodOfAnnotation(String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new ControlDeclarationException("Cannot invoke method " + method + " of annotation " + annotation + ".", e);
		}
	}

	@Override
	public String toString() {
		return "ControlDeclaration [parameterSource=" + parameterSource + ", id=" + id + ", controlType=" + controlType
				+ ", controlParameterType=" + controlParameterType + ", annotation=" + annotation + ", targetElement="
				+ targetElement + ", parameterId=" + parameterSourceId + "]";
	}

}

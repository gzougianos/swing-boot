package io.github.swingboot.control;

import static io.github.swingboot.control.reflect.ReflectionUtils.equalsOrExtends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Optional;

import io.github.swingboot.control.annotation.DeclaresControl;
import io.github.swingboot.control.annotation.installer.AnnotationInstaller;
import io.github.swingboot.control.parameter.ParameterSource;
import io.github.swingboot.control.reflect.ReflectionException;

class ControlInstallationDeclaration implements ControlDeclaration {

	private String id;
	private final Annotation annotation;
	private final AnnotatedElement targetElement;
	private final Class<? extends AnnotationInstaller> installerType;
	private final ParameterSourceContext parameterSourceContext;
	private final ControlTypeInfo controlTypeInfo;

	@SuppressWarnings("unchecked")
	ControlInstallationDeclaration(Annotation annotation, AnnotatedElement targetElement) {
		this.targetElement = targetElement;
		this.annotation = annotation;

		checkIfAnnotationDeclaresControl();
		checkIfAnnotationCanBeInstalledToTargetElement();

		installerType = annotation.annotationType().getAnnotation(DeclaresControl.class).installer();

		id = String.valueOf(invokeMethodOfAnnotation("id"));
		if (id.isEmpty())
			createIdBasedOnElements();

		String parameterSourceId = String.valueOf(invokeMethodOfAnnotation("parameterSource"));
		Class<? extends Control<?>> controlType = (Class<? extends Control<?>>) invokeMethodOfAnnotation(
				"value");

		controlTypeInfo = ControlTypeInfo.of(controlType);
		parameterSourceContext = new ParameterSourceContext(controlTypeInfo, parameterSourceId, this);
		parameterSourceContext.checkIfParameterSourceGivenWhenNonNullableParameter();

	}

	private void createIdBasedOnElements() {
		id = annotation.toString() + targetElement.toString();
	}

	private void checkIfAnnotationDeclaresControl() {
		if (!annotation.annotationType().isAnnotationPresent(DeclaresControl.class))
			throw new InvalidControlDeclarationException(
					annotation.annotationType() + " is not a @DeclaresControl annotation.");
	}

	private void checkIfAnnotationCanBeInstalledToTargetElement() {
		DeclaresControl declaresControl = annotation.annotationType().getAnnotation(DeclaresControl.class);
		Class<?> targetType = getTargetType(targetElement);

		if (!supportsType(declaresControl, targetType)) {
			Class<?> declaringClass = getTargetElementDeclaringClass();
			throw new InvalidControlDeclarationException(annotation + " declared in " + declaringClass
					+ " cannot be installed to objects of type " + targetType);
		}
	}

	private Class<?> getTargetType(AnnotatedElement targetElement) {
		if (targetElement instanceof Field)
			return ((Field) targetElement).getType();
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;

		throw new InvalidControlDeclarationException("Unsupported target element:" + targetElement);
	}

	AnnotatedElement getTargetElement() {
		return targetElement;
	}

	private boolean supportsType(DeclaresControl declaresControl, Class<?> type) {
		for (Class<?> clazz : declaresControl.targetTypes()) {
			if (equalsOrExtends(type, clazz))
				return true;
		}
		return false;
	}

	@Override
	public Optional<ParameterSource> getParameterSource() {
		return parameterSourceContext.getParameterSource();
	}

	@Override
	public boolean expectsParameterSource() {
		return parameterSourceContext.expectsParameterSource();
	}

	@Override
	public void setParameterSource(ParameterSource parameterSource) {
		parameterSourceContext.setParameterSource(parameterSource);
	}

	@Override
	public String getParameterSourceId() {
		return parameterSourceContext.getId();
	}

	public Class<? extends AnnotationInstaller> getInstallerType() {
		return installerType;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ControlTypeInfo getControlTypeInfo() {
		return controlTypeInfo;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	private Class<?> getTargetElementDeclaringClass() {
		if (targetElement instanceof Class<?>)
			return (Class<?>) targetElement;
		if (targetElement instanceof Member)
			return ((Member) targetElement).getDeclaringClass();

		throw new UnsupportedOperationException(
				"Error finding declaring class of target element: " + targetElement);
	}

	private Object invokeMethodOfAnnotation(String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InvalidControlDeclarationException(
					"Cannot invoke method " + method + " of annotation " + annotation + ".", e);
		}
	}

	public Object getInstallationTargetFor(Object owner) {
		if (targetElement instanceof Class<?>)
			return owner;

		return fromField(owner);
	}

	private Object fromField(Object owner) {
		Field targetField = (Field) targetElement;
		try {
			targetField.setAccessible(true);

			Object target = targetField.get(owner);
			if (target == null) {
				throw new NullPointerException(
						"Value of field '" + targetField.getName() + "' declared in class "
								+ targetField.getDeclaringClass().getSimpleName() + " is null.");
			}
			return target;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ReflectionException("Error getting value from target element " + targetElement, e);
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
				parameterSourceContext.getId().isEmpty() ? "<NONE>" : parameterSourceContext.getId(),
				controlTypeInfo.getControlType().getSimpleName(),
				targetElement instanceof Field ? "field '"+ ((Field)targetElement).getName() + "' of class " + clazzName : clazzName );
		//@formatter:on
	}

}

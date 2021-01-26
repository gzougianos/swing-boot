package io.github.swingboot.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.swingboot.control.annotation.DeclaresControl;
import io.github.swingboot.control.annotation.multiple.DeclaresMultipleControls;
import io.github.swingboot.control.reflect.ReflectionException;

final class DeclaresControlAnnotations {
	private DeclaresControlAnnotations() {
	}

	static Set<Annotation> ofElement(AnnotatedElement annotatedElement) {
		Set<Annotation> result = new HashSet<>();

		for (Annotation annotation : getAllAnnotations(annotatedElement)) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (declaresMultipleControls(annotation)) {
				addToResult(result, invokeValueMethodOf(annotation));
			} else if (annotationType.isAnnotationPresent(DeclaresControl.class)) {
				addToResult(result, annotation);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	private static Annotation[] invokeValueMethodOf(Annotation annotation) {
		try {
			return (Annotation[]) annotation.getClass().getMethod("value").invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ReflectionException(
					"Error invoking value method of DeclaresMultipleControls annotation: " + annotation, e);
		}
	}

	private static boolean declaresMultipleControls(Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		return annotationType.isAnnotationPresent(DeclaresMultipleControls.class);
	}

	private static void addToResult(Set<Annotation> result, Annotation... annotations) {
		for (Annotation a : annotations)
			result.add(a);
	}

	private static Set<Annotation> getAllAnnotations(AnnotatedElement annotatedElement) {
		Set<Annotation> annotations = new HashSet<>();
		annotations.addAll(Arrays.asList(annotatedElement.getAnnotations()));
		annotations.addAll(Arrays.asList(annotatedElement.getDeclaredAnnotations()));
		return annotations;
	}
}

package io.github.suice.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.suice.control.annotation.DeclaresControl;
import io.github.suice.control.annotation.multiple.DeclaresMultipleControls;

final class DeclaresControlAnnotations {
	private DeclaresControlAnnotations() {
	}

	public static Set<Annotation> ofElement(AnnotatedElement annotatedElement) {
		Set<Annotation> result = new HashSet<>();

		for (Annotation annotation : getAllAnnotations(annotatedElement)) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (declaresMultipleControls(annotation)) {
				Class<? extends Annotation> typeOfControls = annotationType
						.getAnnotation(DeclaresMultipleControls.class).value();

				Annotation[] nestedDeclaresControlAnnotations = annotatedElement
						.getAnnotationsByType(typeOfControls);

				addToResult(result, nestedDeclaresControlAnnotations);
			} else if (annotationType.isAnnotationPresent(DeclaresControl.class)) {
				addToResult(result, annotation);
			}
		}

		return Collections.unmodifiableSet(result);
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

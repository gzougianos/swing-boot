package io.github.suice.command;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionSupport {
	private static final Logger log = LoggerFactory.getLogger(ReflectionSupport.class);

	public static boolean hasMethod(String methodName, Class<?> type) {
		Predicate<Method> methodNameEquals = method -> method.getName().equals(methodName);
		return asList(type.getMethods()).stream().anyMatch(methodNameEquals)
				|| asList(type.getDeclaredMethods()).stream().anyMatch(methodNameEquals);
	}

	/**
	 * 
	 * @param clazz The class to look for fields.
	 * @param annotationType The type of the annotation that declares this class contains relevant fields.
	 * @return The declared (private/protected/public) fields of the class and the inherited fields from superclasses (public/protected).
	 */
	public static Set<Field> getDeclaredAndInheritedFields(Class<?> clazz, Class<? extends Annotation> annotationType) {
		Set<Field> fields = new HashSet<>();
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isStatic(field.getModifiers()) || field.isSynthetic())
					continue;

				fields.add(field);
			}

			clazz = clazz.getSuperclass();
			if (!clazz.isAnnotationPresent(annotationType))
				break;
		}
		return Collections.unmodifiableSet(fields);
	}

}

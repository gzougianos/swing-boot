package io.github.suice.command;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public final class ReflectionUtils {
	private ReflectionUtils() {
	}

	public static boolean equalsOrExtends(Class<?> class1, Class<?> class2) {
		return class1.equals(class2) || class2.isAssignableFrom(class1);
	}

	public static boolean hasMethod(String methodName, Class<?> type) {
		Predicate<Method> methodNameEquals = method -> method.getName().equals(methodName);
		return asList(type.getMethods()).stream().anyMatch(methodNameEquals)
				|| asList(type.getDeclaredMethods()).stream().anyMatch(methodNameEquals);
	}
}

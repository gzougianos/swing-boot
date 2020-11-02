package io.github.suice.command.reflect;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

import io.github.suice.command.Command;

public final class ReflectionUtils {
	private static final Map<Class<? extends Command<?>>, Class<?>> commandTypesWithParameterTypes = new HashMap<>();
	//@formatter:off
	private static final Class<?>[][] primitives = {
			{Integer.class, int.class},
			{Double.class, double.class},
			{Float.class, float.class},
			{Short.class, short.class},
			{Character.class, char.class},
		};
	//@formatter:on
	private ReflectionUtils() {
	}

	public static boolean equalsOrExtends(Class<?> class1, Class<?> class2) {
		return class1.equals(class2) || class2.isAssignableFrom(class1) || arePrimitiveEquals(class1, class2);
	}

	private static boolean arePrimitiveEquals(Class<?> class1, Class<?> class2) {
		for (Class<?>[] clazzes : primitives) {
			if (class1.equals(clazzes[0]) && class2.equals(clazzes[1]))
				return true;

			if (class2.equals(clazzes[0]) && class1.equals(clazzes[1]))
				return true;
		}
		return false;
	}

	public static boolean hasMethod(String methodName, Class<?> type) {
		Predicate<Method> methodNameEquals = method -> method.getName().equals(methodName);
		return asList(type.getMethods()).stream().anyMatch(methodNameEquals)
				|| asList(type.getDeclaredMethods()).stream().anyMatch(methodNameEquals);
	}

	public static Class<?> getCommandGenericParameterType(Class<? extends Command<?>> commandType) {
		if (commandTypesWithParameterTypes.containsKey(commandType))
			return commandTypesWithParameterTypes.get(commandType);

		for (TypeToken<?> typeToken : TypeToken.of(commandType).getTypes()) {
			Class<?> rawType = typeToken.getRawType();
			if (rawType == Command.class) {
				TypeLiteral<?> typeLiteral = TypeLiteral.get(typeToken.getType());
				TypeLiteral<?> methodParameterTypeLiteral = typeLiteral.getParameterTypes(rawType.getMethods()[0]).get(0);
				Class<?> parType = methodParameterTypeLiteral.getRawType();
				commandTypesWithParameterTypes.put(commandType, parType);
				return parType;
			}
		}
		commandTypesWithParameterTypes.put(commandType, Object.class);
		return Object.class;
	}
}

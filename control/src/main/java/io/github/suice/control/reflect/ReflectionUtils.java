package io.github.suice.control.reflect;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

import io.github.suice.control.Control;

public final class ReflectionUtils {
	private static final Map<Class<? extends Control<?>>, Class<?>> controlParameterTypes = new HashMap<>();
	//@formatter:off
	private static final Class<?>[][] primitives = {
			{Integer.class, int.class},
			{Double.class, double.class},
			{Float.class, float.class},
			{Short.class, short.class},
			{Long.class, long.class},
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

	public static Class<?> getControlParameterType(Class<? extends Control<?>> controlType) {
		if (controlParameterTypes.containsKey(controlType))
			return controlParameterTypes.get(controlType);

		for (TypeToken<?> typeToken : TypeToken.of(controlType).getTypes()) {
			Class<?> rawType = typeToken.getRawType();
			if (rawType == Control.class) {
				TypeLiteral<?> typeLiteral = TypeLiteral.get(typeToken.getType());
				TypeLiteral<?> methodParameterTypeLiteral = typeLiteral.getParameterTypes(rawType.getMethods()[0]).get(0);
				Class<?> parType = methodParameterTypeLiteral.getRawType();
				controlParameterTypes.put(controlType, parType);
				return parType;
			}
		}
		controlParameterTypes.put(controlType, Object.class);
		return Object.class;
	}
}

package io.github.swingboot.control.reflect;

public final class ReflectionUtils {
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

}

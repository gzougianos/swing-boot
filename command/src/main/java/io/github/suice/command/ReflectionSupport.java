package io.github.suice.command;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;

import java.awt.AWTEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;

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

	public static Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> defineParameterizedCommandTypes(
			Class<? extends Command<?>> commands[]) {
		Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> map = new HashMap<>();
		for (Class<? extends Command<?>> cmdType : commands) {
			map.put(cmdType, getParameterizedTypeOfOptional(cmdType));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends AWTEvent> getParameterizedTypeOfOptional(Class<? extends Command<?>> commandType) {
		try {
			Parameter parameter = commandType.getMethod("execute", Optional.class).getParameters()[0];
			TypeLiteral<?> literal = TypeLiteral.get(parameter.getParameterizedType());
			Class<?> parType = literal.getFieldType(Optional.class.getDeclaredField("value")).getRawType();
			if (AWTEvent.class.isAssignableFrom(parType))
				return ((Class<? extends AWTEvent>) parType);
		} catch (NoSuchMethodException | NoSuchFieldException e) {
			log.error("Error checking if command type is event parametrized.", e);
		}
		return null;
	}
}

package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.suice.command.annotation.ParameterSource;
import io.github.suice.command.exception.InvalidParameterSourceException;
import io.github.suice.command.reflect.FieldOrMethod;

public class ParameterSourceScan {
	private Map<String, FieldOrMethod> parameterSources = new HashMap<>();
	private Class<?> clazz;

	public ParameterSourceScan(Class<?> clazz) {
		this.clazz = clazz;

		scanForFieldParameterSources();
		scanForMethodParameterSources();

		inheritFromParents();
		parameterSources = Collections.unmodifiableMap(parameterSources);
	}

	private void inheritFromParents() {
		if (ignoresAllFromParent())
			return;

		Set<String> ignoredIds = getIgnoredIds();

		Class<?> parentClass = clazz.getSuperclass();
		if (parentClass.isAnnotationPresent(InstallCommands.class)) {
			ParameterSourceScan parentScan = new ParameterSourceScan(parentClass);
			parentScan.getParameterSources().forEach((id, fieldOrMethod) -> {
				if (!ignoredIds.contains(id) && !parameterSources.containsKey(id))
					parameterSources.put(id, fieldOrMethod);
			});
		}
	}

	private boolean ignoresAllFromParent() {
		return clazz.isAnnotationPresent(InstallCommands.class)
				&& clazz.getAnnotation(InstallCommands.class).ignoreAllIdsFromParent();
	}

	private Set<String> getIgnoredIds() {
		if (!clazz.isAnnotationPresent(InstallCommands.class))
			return new HashSet<>();

		InstallCommands installCommands = clazz.getAnnotation(InstallCommands.class);
		return new HashSet<>(Arrays.asList(installCommands.ignoreIdsFromParent()));
	}

	public Map<String, FieldOrMethod> getParameterSources() {
		return parameterSources;
	}

	private void scanForMethodParameterSources() {
		for (Method method : clazz.getDeclaredMethods()) {
			if (!hasAnyAnnotations(method))
				continue;

			if (method.isAnnotationPresent(ParameterSource.class)) {
				if (!hasZeroOrOneAwtEventParameter(method)) {
					throw new InvalidParameterSourceException("ParameterSource method " + method.getName() + " in class"
							+ clazz.getSimpleName() + " can have zero or only one AWTEvent parameter.");
				}

				addParameterSource(method);
			}
		}
	}

	private boolean hasZeroOrOneAwtEventParameter(Method method) {
		if (method.getParameterCount() == 0)
			return true;

		if (method.getParameterCount() == 1) {
			Parameter parameter = method.getParameters()[0];
			if (equalsOrExtends(parameter.getType(), AWTEvent.class)) {
				return true;
			}
		}
		return false;
	}

	private void scanForFieldParameterSources() {
		for (Field field : clazz.getDeclaredFields()) {
			if (!hasAnyAnnotations(field) || field.isSynthetic())
				continue;

			if (field.isAnnotationPresent(ParameterSource.class)) {
				addParameterSource(field);
			}
		}
	}

	private void addParameterSource(AccessibleObject accessibleObject) {
		ParameterSource parameterSource = accessibleObject.getAnnotation(ParameterSource.class);
		String sourceId = parameterSource.value();
		if (parameterSources.containsKey(sourceId))
			throw new InvalidParameterSourceException(
					"More than one @ParameterSource declared with id `" + sourceId + "` in " + clazz + ".");

		parameterSources.put(sourceId, new FieldOrMethod(accessibleObject));
	}

	private boolean hasAnyAnnotations(AnnotatedElement annotatedElement) {
		return annotatedElement.getDeclaredAnnotations().length > 0 || annotatedElement.getAnnotations().length > 0;
	}
}

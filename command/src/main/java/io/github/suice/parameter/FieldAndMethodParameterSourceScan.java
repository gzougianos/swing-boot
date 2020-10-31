package io.github.suice.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.suice.command.InstallCommands;

public class FieldAndMethodParameterSourceScan {
	private Map<String, ParameterSource> parameterSources = new HashMap<>();
	private Class<?> clazz;

	public FieldAndMethodParameterSourceScan(Class<?> clazz) {
		this.clazz = clazz;

		scanForParamaterSources(clazz.getDeclaredMethods());
		scanForParamaterSources(clazz.getDeclaredFields());

		inheritFromParents();
		parameterSources = Collections.unmodifiableMap(parameterSources);
	}

	private void inheritFromParents() {
		if (ignoresAllFromParent())
			return;

		Set<String> ignoredIds = getIgnoredIds();

		Class<?> parentClass = clazz.getSuperclass();
		if (parentClass.isAnnotationPresent(InstallCommands.class)) {
			FieldAndMethodParameterSourceScan parentScan = new FieldAndMethodParameterSourceScan(parentClass);
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

	public Map<String, ParameterSource> getParameterSources() {
		return parameterSources;
	}

	private <T extends Member & AnnotatedElement> void scanForParamaterSources(T[] members) {
		for (T member : members) {
			if (!hasAnyAnnotations(member) || member.isSynthetic())
				continue;

			if (member.isAnnotationPresent(io.github.suice.command.annotation.ParameterSource.class)) {
				addParameterSource(member);
			}
		}
	}

	private void addParameterSource(AnnotatedElement accessibleObject) {
		io.github.suice.command.annotation.ParameterSource parameterSource = accessibleObject
				.getAnnotation(io.github.suice.command.annotation.ParameterSource.class);

		String sourceId = parameterSource.value();

		if (parameterSources.containsKey(sourceId))
			throw new ParameterSourceException(
					"More than one @ParameterSource declared with id `" + sourceId + "` in " + clazz + ".");

		parameterSources.put(sourceId, createInvokable(sourceId, accessibleObject));
	}

	private ParameterSource createInvokable(String id, AnnotatedElement accessibleObject) {
		if (accessibleObject instanceof Field)
			return new FieldParameterSource(id, (Field) accessibleObject);
		else if (accessibleObject instanceof Method)
			return new MethodParameterSource(id, (Method) accessibleObject);

		throw new UnsupportedOperationException(accessibleObject + " is not a field or a method.");
	}

	private boolean hasAnyAnnotations(AnnotatedElement annotatedElement) {
		return annotatedElement.getDeclaredAnnotations().length > 0 || annotatedElement.getAnnotations().length > 0;
	}
}

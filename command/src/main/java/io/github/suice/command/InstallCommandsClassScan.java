package io.github.suice.command;

import static java.lang.reflect.Modifier.isStatic;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class InstallCommandsClassScan {

	private Class<?> clazz;
	private Set<Field> annotatedComponentFields;

	public InstallCommandsClassScan(Class<?> clazz) {
		this.clazz = clazz;

		annotatedComponentFields = new HashSet<>();
		scanFields();

		annotatedComponentFields = Collections.unmodifiableSet(annotatedComponentFields);
	}

	public Set<Field> getAnnotatedComponentFields() {
		return annotatedComponentFields;
	}

	private void scanFields() {
		Set<Field> allFields = getNonStaticDeclaredAndInheritedFields();

		//@formatter:off
		annotatedComponentFields = allFields.stream()
				.filter(this::isComponentField)
				.filter(this::hasAnnotations)
				.collect(Collectors.toSet());
		//@formatter:on
	}

	private boolean isComponentField(Field field) {
		return ReflectionUtils.equalsOrExtends(field.getType(), Component.class);
	}

	private boolean hasAnnotations(Field field) {
		return field.getDeclaredAnnotations().length > 0;
	}

	private Set<Field> getNonStaticDeclaredAndInheritedFields() {
		Set<Field> fields = new HashSet<>();
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isStatic(field.getModifiers()) || field.isSynthetic())
					continue;

				fields.add(field);
			}

			clazz = clazz.getSuperclass();
			if (!clazz.isAnnotationPresent(InstallCommands.class))
				break;
		}
		return Collections.unmodifiableSet(fields);
	}

}

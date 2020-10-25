package io.github.suice.command;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.command.annotation.DeclaresCommand;
import io.github.suice.command.reflect.ReflectionUtils;

public class InstallCommandsClassAnalysis {

	private static final Logger log = LoggerFactory.getLogger(InstallCommandsClassAnalysis.class);
	private Class<?> clazz;
	private Map<String, CommandDeclaration> commandDeclarations;
	private ParameterSourceScan parameterSourceScan;

	public InstallCommandsClassAnalysis(Class<?> clazz) {
		this.clazz = clazz;

		commandDeclarations = new HashMap<>();
		scanCommandDeclarationsOnFields();
		scanCommandDeclarationsOnType();

		parameterSourceScan = new ParameterSourceScan(clazz);

		inheritCommandDeclarationsFromParents();

		bindParameterSourcesToCommandDeclarations();

		commandDeclarations = Collections.unmodifiableMap(commandDeclarations);
	}

	private void scanCommandDeclarationsOnType() {
		//TODO: ignore from parent
		for (Annotation annotation : getDeclaresCommandAnnotations(clazz)) {
			CommandDeclaration cmdDeclaration = new CommandDeclaration(annotation, clazz);
			commandDeclarations.put(cmdDeclaration.getId(), cmdDeclaration);
		}
	}

	private void scanCommandDeclarationsOnFields() {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isSynthetic()) //added by compiler
				continue;

			if (!hasAnyAnnotations(field))
				continue;

			boolean isStaticField = Modifier.isStatic(field.getModifiers());
			if (isStaticField || !isComponentField(field))
				continue;

			for (Annotation annotation : getDeclaresCommandAnnotations(field)) {
				CommandDeclaration cmdDeclaration = new CommandDeclaration(annotation, field);
				commandDeclarations.put(cmdDeclaration.getId(), cmdDeclaration);
			}
		}
	}

	public Map<String, CommandDeclaration> getCommandDeclarations() {
		return commandDeclarations;
	}

	private void bindParameterSourcesToCommandDeclarations() {
		parameterSourceScan.getParameterSources().forEach((id, fieldOrMethod) -> {
			CommandDeclaration cmdDeclaration = commandDeclarations.get(id);
			if (cmdDeclaration == null) {
				log.warn("@ParameterSource with id `" + id + "` in " + clazz + " does not match any command declaration id.");
			} else {
				cmdDeclaration.setParameterSource(fieldOrMethod);
			}
		});
	}

	private void inheritCommandDeclarationsFromParents() {
		if (ignoresAllIdsFromParent())
			return;

		Class<?> parentClass = clazz.getSuperclass();
		if (!parentClass.isAnnotationPresent(InstallCommands.class))
			return;

		Set<String> ignoreInheritedIds = getIgnoredIds(clazz);

		InstallCommandsClassAnalysis parentScan = new InstallCommandsClassAnalysis(parentClass);
		parentScan.commandDeclarations.forEach((id, declaration) -> {
			if (!ignoreInheritedIds.contains(id) && !commandDeclarations.containsKey(id))
				commandDeclarations.put(id, declaration);
		});

	}

	private boolean ignoresAllIdsFromParent() {
		if (clazz.isAnnotationPresent(InstallCommands.class)) {
			InstallCommands installCommands = clazz.getAnnotation(InstallCommands.class);
			return installCommands.ignoreAllIdsFromParent();
		}
		return false;
	}

	private Set<String> getIgnoredIds(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(InstallCommands.class))
			return new HashSet<>();

		InstallCommands installCommands = clazz.getAnnotation(InstallCommands.class);
		return new HashSet<>(Arrays.asList(installCommands.ignoreIdsFromParent()));
	}

	private Set<Annotation> getDeclaresCommandAnnotations(AnnotatedElement annotatedElement) {
		//@formatter:off
		return getAllAnnotations(annotatedElement).stream()
				.filter(a -> a.annotationType().isAnnotationPresent(DeclaresCommand.class))
				.collect(Collectors.toSet());
		//@formatter:on
	}

	private Set<Annotation> getAllAnnotations(AnnotatedElement annotatedElement) {
		Set<Annotation> annotations = new HashSet<>();
		annotations.addAll(Arrays.asList(annotatedElement.getAnnotations()));
		annotations.addAll(Arrays.asList(annotatedElement.getDeclaredAnnotations()));
		return annotations;
	}

	private boolean hasAnyAnnotations(AnnotatedElement annotatedElement) {
		return annotatedElement.getDeclaredAnnotations().length > 0 || annotatedElement.getAnnotations().length > 0;
	}

	private boolean isComponentField(Field field) {
		return ReflectionUtils.equalsOrExtends(field.getType(), Component.class);
	}
}

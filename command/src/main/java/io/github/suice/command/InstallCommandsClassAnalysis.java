package io.github.suice.command;

import static io.github.suice.command.annotation.ParameterSource.THIS;

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

import io.github.suice.command.annotation.DeclaresCommand;
import io.github.suice.command.exception.InvalidCommandDeclarationException;
import io.github.suice.command.reflect.ReflectionUtils;
import io.github.suice.parameter.FieldAndMethodParameterSourceScan;
import io.github.suice.parameter.ParameterSource;
import io.github.suice.parameter.SourceOwnerParameterSource;

public class InstallCommandsClassAnalysis {
	private final Class<?> clazz;
	private Map<String, CommandDeclaration> commandDeclarations;
	private FieldAndMethodParameterSourceScan fieldAndMethodParameterSourceScan;

	public InstallCommandsClassAnalysis(Class<?> clazz) {
		this.clazz = clazz;

		checkInstallCommandsAnnotationIsPresent();

		commandDeclarations = new HashMap<>();
		scanCommandDeclarationsOnFields();
		scanCommandDeclarationsOnType();

		fieldAndMethodParameterSourceScan = new FieldAndMethodParameterSourceScan(clazz);

		inheritCommandDeclarationsFromParents();

		bindParameterSourcesToCommandDeclarations();

		commandDeclarations = Collections.unmodifiableMap(commandDeclarations);
	}

	private void checkInstallCommandsAnnotationIsPresent() {
		if (!clazz.isAnnotationPresent(InstallCommands.class))
			throw new IllegalArgumentException(clazz + " is not annonated with @InstallCommands.");
	}

	private void scanCommandDeclarationsOnType() {
		for (Annotation annotation : getDeclaresCommandAnnotations(clazz)) {
			CommandDeclaration cmdDeclaration = new CommandDeclaration(annotation, clazz);
			checkIfNotAlreadyExists(cmdDeclaration.getId());
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

				checkIfNotAlreadyExists(cmdDeclaration.getId());
				commandDeclarations.put(cmdDeclaration.getId(), cmdDeclaration);
			}
		}
	}

	private void checkIfNotAlreadyExists(String id) {
		if (commandDeclarations.containsKey(id)) {
			throw new InvalidCommandDeclarationException("More than 2 commands declared with id `" + id + "` in " + clazz + ".");
		}
	}

	public Map<String, CommandDeclaration> getCommandDeclarations() {
		return commandDeclarations;
	}

	private void bindParameterSourcesToCommandDeclarations() {
		commandDeclarations.values().stream().filter(CommandDeclaration::expectsParameterSource).forEach(declaration -> {
			String expectedParameterSourceId = declaration.getParameterSourceId();
			if (expectedParameterSourceId.equals(THIS)) {
				declaration.setParameterSource(new SourceOwnerParameterSource(clazz));
			} else {
				ParameterSource parSource = fieldAndMethodParameterSourceScan.getParameterSources()
						.get(expectedParameterSourceId);
				if (parSource == null)
					throw new InvalidCommandDeclarationException(
							"@ParameterSource(" + expectedParameterSourceId + ") not found in " + clazz + ".");
				declaration.setParameterSource(parSource);
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
			if (ignoreInheritedIds.contains(id))
				return;

			if (commandDeclarations.containsKey(id))
				throw new InvalidCommandDeclarationException(
						"Command with id '" + id + "' in " + clazz + " is already declared in parent " + parentScan.clazz + ".");

			commandDeclarations.put(id, declaration);
		});

	}

	private boolean ignoresAllIdsFromParent() {
		InstallCommands installCommands = clazz.getAnnotation(InstallCommands.class);
		return installCommands.ignoreAllIdsFromParent();
	}

	private Set<String> getIgnoredIds(Class<?> clazz) {
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

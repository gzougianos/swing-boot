package io.github.swingboot.control.declaration;

import static io.github.swingboot.control.ParameterSource.THIS;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.swingboot.control.InitializedBy;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.declaration.parameter.FieldAndMethodParameterSourceScan;
import io.github.swingboot.control.declaration.parameter.ParameterSource;
import io.github.swingboot.control.declaration.parameter.SourceOwnerParameterSource;

public class InstallControlsClassAnalysis {
	private static final Map<Class<?>, InstallControlsClassAnalysis> cache = new HashMap<>();
	private final Class<?> clazz;
	private Map<String, ControlInstallationDeclaration> controlDeclarations;
	private FieldAndMethodParameterSourceScan fieldAndMethodParameterSourceScan;
	private Set<Field> nestedInstallControlsFields;
	private InitializedByDeclaration initializedByDeclaration;

	private InstallControlsClassAnalysis(Class<?> clazz) {
		this.clazz = clazz;

		controlDeclarations = new HashMap<>();

		Set<Field> nonStaticFieldsWithAtLeastOneAnnotation = getNonStaticFieldsWithAtLeastOneAnnotation();
		nonStaticFieldsWithAtLeastOneAnnotation.forEach(this::putControlDeclarationsOfElement);
		putControlDeclarationsOfElement(clazz);

		nestedInstallControlsFields = getNestedInstallControlFields(nonStaticFieldsWithAtLeastOneAnnotation);
		nestedInstallControlsFields = Collections.unmodifiableSet(nestedInstallControlsFields);

		fieldAndMethodParameterSourceScan = FieldAndMethodParameterSourceScan.of(clazz);

		if (clazz.isAnnotationPresent(InstallControls.class))
			inheritControlDeclarationsFromParents();

		bindParameterSourcesToControlDeclarations();

		initializeInitializedByDeclaration();

		controlDeclarations = Collections.unmodifiableMap(controlDeclarations);
	}

	private void initializeInitializedByDeclaration() {
		if (clazz.isAnnotationPresent(InitializedBy.class)) {
			initializedByDeclaration = new InitializedByDeclaration(clazz,
					clazz.getAnnotation(InitializedBy.class));
			if (initializedByDeclaration.expectsParameterSource())
				bindParameterSourceTo(initializedByDeclaration);
		}
	}

	public Optional<InitializedByDeclaration> getInitializedByDeclaration() {
		return Optional.ofNullable(initializedByDeclaration);
	}

	private Set<Field> getNestedInstallControlFields(Set<Field> nonStaticFieldsWithAtLeastOneAnnotation) {
		//@formatter:off
		return nonStaticFieldsWithAtLeastOneAnnotation.stream()
				.filter(f -> f.isAnnotationPresent(InstallControls.class))
				.collect(Collectors.toSet());
		//@formatter:on
	}

	private Set<Field> getNonStaticFieldsWithAtLeastOneAnnotation() {
		Set<Field> result = new HashSet<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isSynthetic()) //added by compiler
				continue;

			if (!hasAnyAnnotations(field) || Modifier.isStatic(field.getModifiers()))
				continue;

			result.add(field);
		}
		return result;
	}

	private void putControlDeclarationsOfElement(AnnotatedElement element) {
		Set<Annotation> declaresControlAnnotations = DeclaresControlInstallationAnnotations
				.ofElement(element);
		for (Annotation annotation : declaresControlAnnotations) {
			ControlInstallationDeclaration controlDeclaration = new ControlInstallationDeclaration(annotation,
					element);

			checkIfNotAlreadyExists(controlDeclaration.getId());
			controlDeclarations.put(controlDeclaration.getId(), controlDeclaration);
		}
	}

	private void checkIfNotAlreadyExists(String id) {
		if (controlDeclarations.containsKey(id)) {
			throw new InvalidControlDeclarationException(
					"More than 2 controls declared with id `" + id + "` in " + clazz + ".");
		}
	}

	public Map<String, ControlInstallationDeclaration> getControlDeclarations() {
		return controlDeclarations;
	}

	public Set<Field> getNestedInstallControlsFields() {
		return nestedInstallControlsFields;
	}

	private void bindParameterSourcesToControlDeclarations() {
		controlDeclarations.values().stream().filter(ControlInstallationDeclaration::expectsParameterSource)
				.forEach(this::bindParameterSourceTo);
	}

	private void bindParameterSourceTo(ControlDeclaration declaration) {
		String expectedParameterSourceId = declaration.getParameterSourceId();
		if (expectedParameterSourceId.equals(THIS)) {
			declaration.setParameterSource(new SourceOwnerParameterSource(clazz));
		} else {
			ParameterSource parSource = fieldAndMethodParameterSourceScan.getParameterSources()
					.get(expectedParameterSourceId);
			if (parSource == null) {
				throw new InvalidControlDeclarationException(
						"No @ParameterSource(" + expectedParameterSourceId + ") found in class "
								+ clazz.getSimpleName() + " for " + declaration + ".");
			}
			declaration.setParameterSource(parSource);
		}
	}

	private void inheritControlDeclarationsFromParents() {
		if (ignoresAllIdsFromParent())
			return;

		Class<?> parentClass = clazz.getSuperclass();
		if (!parentClass.isAnnotationPresent(InstallControls.class))
			return;

		Set<String> ignoreInheritedIds = getIgnoredIds(clazz);

		InstallControlsClassAnalysis parentScan = of(parentClass);
		parentScan.controlDeclarations.forEach((id, declaration) -> {
			if (ignoreInheritedIds.contains(id))
				return;

			if (controlDeclarations.containsKey(id))
				throw new InvalidControlDeclarationException("Control with id '" + id + "' in " + clazz
						+ " is already declared in parent " + parentScan.clazz + ".");

			controlDeclarations.put(id, declaration);
		});

	}

	private boolean ignoresAllIdsFromParent() {
		InstallControls installControls = clazz.getAnnotation(InstallControls.class);
		return installControls.ignoreAllIdsFromParent();
	}

	private Set<String> getIgnoredIds(Class<?> clazz) {
		InstallControls installControls = clazz.getAnnotation(InstallControls.class);
		return new HashSet<>(Arrays.asList(installControls.ignoreIdsFromParent()));
	}

	private boolean hasAnyAnnotations(AnnotatedElement annotatedElement) {
		return annotatedElement.getDeclaredAnnotations().length > 0
				|| annotatedElement.getAnnotations().length > 0;
	}

	public static InstallControlsClassAnalysis of(Class<?> clazz) {
		return fromCacheOrNew(clazz);
	}

	private static InstallControlsClassAnalysis fromCacheOrNew(Class<?> clazz) {
		if (cache.containsKey(clazz))
			return cache.get(clazz);

		InstallControlsClassAnalysis analysis = new InstallControlsClassAnalysis(clazz);
		cache.put(clazz, analysis);
		return analysis;
	}
}

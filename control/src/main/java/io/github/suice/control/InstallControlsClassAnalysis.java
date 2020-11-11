package io.github.suice.control;

import static io.github.suice.control.annotation.ParameterSource.THIS;

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

import io.github.suice.control.annotation.DeclaresControl;
import io.github.suice.control.annotation.InstallControls;
import io.github.suice.control.parameter.FieldAndMethodParameterSourceScan;
import io.github.suice.control.parameter.ParameterSource;
import io.github.suice.control.parameter.SourceOwnerParameterSource;
import io.github.suice.control.reflect.ReflectionUtils;

public class InstallControlsClassAnalysis {
	private static final Map<Class<?>, InstallControlsClassAnalysis> cache = new HashMap<>();
	private final Class<?> clazz;
	private Map<String, ControlDeclaration> controlDeclarations;
	private FieldAndMethodParameterSourceScan fieldAndMethodParameterSourceScan;

	private InstallControlsClassAnalysis(Class<?> clazz) {
		this.clazz = clazz;

		checkInstallControlsAnnotationIsPresent();

		controlDeclarations = new HashMap<>();
		scanControlDeclarationsOnFields();
		scanControlDeclarationsOnType();

		fieldAndMethodParameterSourceScan = new FieldAndMethodParameterSourceScan(clazz);

		inheritControlDeclarationsFromParents();

		bindParameterSourcesToControlDeclarations();

		controlDeclarations = Collections.unmodifiableMap(controlDeclarations);
	}

	private void checkInstallControlsAnnotationIsPresent() {
		if (!clazz.isAnnotationPresent(InstallControls.class))
			throw new IllegalArgumentException(clazz + " is not annonated with @InstallControls.");
	}

	private void scanControlDeclarationsOnType() {
		for (Annotation annotation : getDeclaresControlAnnotations(clazz)) {
			ControlDeclaration controlDeclaration = new ControlDeclaration(annotation, clazz);
			checkIfNotAlreadyExists(controlDeclaration.getId());
			controlDeclarations.put(controlDeclaration.getId(), controlDeclaration);
		}
	}

	private void scanControlDeclarationsOnFields() {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isSynthetic()) //added by compiler
				continue;

			if (!hasAnyAnnotations(field))
				continue;

			boolean isStaticField = Modifier.isStatic(field.getModifiers());
			if (isStaticField || !isComponentField(field))
				continue;

			for (Annotation annotation : getDeclaresControlAnnotations(field)) {
				ControlDeclaration controlDeclaration = new ControlDeclaration(annotation, field);

				checkIfNotAlreadyExists(controlDeclaration.getId());
				controlDeclarations.put(controlDeclaration.getId(), controlDeclaration);
			}
		}
	}

	private void checkIfNotAlreadyExists(String id) {
		if (controlDeclarations.containsKey(id)) {
			throw new ControlDeclarationException("More than 2 controls declared with id `" + id + "` in " + clazz + ".");
		}
	}

	public Map<String, ControlDeclaration> getControlDeclarations() {
		return controlDeclarations;
	}

	private void bindParameterSourcesToControlDeclarations() {
		controlDeclarations.values().stream().filter(ControlDeclaration::expectsParameterSource).forEach(declaration -> {
			String expectedParameterSourceId = declaration.getParameterSourceId();
			if (expectedParameterSourceId.equals(THIS)) {
				declaration.setParameterSource(new SourceOwnerParameterSource(clazz));
			} else {
				ParameterSource parSource = fieldAndMethodParameterSourceScan.getParameterSources()
						.get(expectedParameterSourceId);
				if (parSource == null) {
					throw new ControlDeclarationException("No @ParameterSource(" + expectedParameterSourceId + ") found in class "
							+ clazz.getSimpleName() + " for " + declaration + ".");
				}
				declaration.setParameterSource(parSource);
			}
		});
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
				throw new ControlDeclarationException(
						"Control with id '" + id + "' in " + clazz + " is already declared in parent " + parentScan.clazz + ".");

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

	private Set<Annotation> getDeclaresControlAnnotations(AnnotatedElement annotatedElement) {
		//@formatter:off
		return getAllAnnotations(annotatedElement).stream()
				.filter(a -> a.annotationType().isAnnotationPresent(DeclaresControl.class))
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

	static InstallControlsClassAnalysis of(Class<?> clazz) {
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

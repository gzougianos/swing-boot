package io.github.swingboot.control;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.swingboot.control.annotation.installer.AnnotationInstaller;
import io.github.swingboot.control.annotation.installer.AnnotationInstallerFactory;
import io.github.swingboot.control.annotation.installer.ControlInstallation;
import io.github.swingboot.control.reflect.ReflectionException;

public class ControlInstaller {
	private static final Logger log = LoggerFactory.getLogger(ControlInstaller.class);
	private Map<Object, List<ControlInstallation>> installationsByObject = new WeakHashMap<>();
	private final Controls controls;

	@Inject
	public ControlInstaller(Controls controls) {
		this.controls = controls;
	}

	public void installControls(Object object) {
		requireNonNull(object, "Cannot install controls to null object.");

		if (alreadyInstalled(object)) {
			return;
		}

		warnNonEdtInstallation(object);

		Class<?> objectType = object.getClass();

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).beforeAnyControlInstalled(controls);
		}

		InstallControlsClassAnalysis classAnalysis = InstallControlsClassAnalysis.of(objectType);
		createInstallationsAndInstall(object, classAnalysis);

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).afterAllControlsInstalled(controls);
		}

		classAnalysis.getNestedInstallControlsFields().forEach(field -> installNestedControls(object, field));
	}

	public void uninstallFrom(Object obj) {
		getInstallationsOrThrow(obj).forEach(ControlInstallation::uninstall);
	}

	public void reinstallTo(Object obj) {
		getInstallationsOrThrow(obj).forEach(ControlInstallation::install);
	}

	private List<ControlInstallation> getInstallationsOrThrow(Object obj) {
		List<ControlInstallation> list = installationsByObject.get(obj);
		if (list == null)
			throw new ControlsWereNeverInstalledException("Controls were never installed to object: " + obj);

		return list;
	}

	private void installNestedControls(Object object, Field field) {
		try {
			field.setAccessible(true);
			Object target = field.get(object);
			ensureNotNullTargetIfItCameFromField(target, field);
			installControls(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			String msg = "Error accessing nested @InstallControls field %s of %s.";
			msg = String.format(msg, field.getName(), field.getDeclaringClass());
			throw new ReflectionException(msg, e);
		}
	}

	private void createInstallationsAndInstall(Object owner, InstallControlsClassAnalysis classAnalysis) {
		//@formatter:off
		List<ControlInstallation> installations = classAnalysis.getControlDeclarations()
				.values().stream()
				.map(controlDeclaration-> new ObjectOwnedControlDeclaration(owner, controlDeclaration))
				.map(this::createInstallation)
				.collect(Collectors.toList());
		//@formatter:on

		installationsByObject.put(owner, Collections.unmodifiableList(installations));
		reinstallTo(owner); //actually the first installation
	}

	private ControlInstallation createInstallation(ObjectOwnedControlDeclaration declaration) {
		AnnotationInstaller installer = AnnotationInstallerFactory.get(declaration.getInstallerType());

		ControlDeclarationPerformer controlPerformer = new ControlDeclarationPerformer(controls, declaration);
		Object target = declaration.getTargetObject();

		ensureNotNullTargetIfItCameFromField(target, declaration.getTargetElement());

		return installer.createInstallation(declaration.getAnnotation(), target, controlPerformer::perform);
	}

	private void ensureNotNullTargetIfItCameFromField(Object target, AnnotatedElement element) {
		if (!(element instanceof Field))
			return;

		Field targetField = (Field) element;

		if (target == null) {
			throw new NullPointerException("Value of field '" + targetField.getName() + "' declared in class "
					+ targetField.getDeclaringClass().getSimpleName() + " is null.");
		}
	}

	private void warnNonEdtInstallation(Object object) {
		boolean runsOnEdt = SwingUtilities.isEventDispatchThread();
		if (!runsOnEdt) {
			log.warn("Installing controls to " + object
					+ " outside the event dispatch thread. All controls should be installed in the event dispatch thread.");
		}
	}

	private boolean alreadyInstalled(Object object) {
		return installationsByObject.containsKey(object);
	}

	public static class ControlsWereNeverInstalledException extends RuntimeException {
		private static final long serialVersionUID = -420040964800757920L;

		private ControlsWereNeverInstalledException(String message) {
			super(message);
		}

	}
}

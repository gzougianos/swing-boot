package io.github.swingboot.control;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.swingboot.control.annotation.installer.AnnotationInstaller;
import io.github.swingboot.control.annotation.installer.AnnotationInstallerFactory;
import io.github.swingboot.control.reflect.ReflectionException;

public class ControlInstaller {
	private static final Logger log = LoggerFactory.getLogger(ControlInstaller.class);
	private static final WeakHashMap<Object, Void> installedObjects = new WeakHashMap<>();
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

		installedObjects.put(object, null);

		Class<?> objectType = object.getClass();

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).beforeAnyControlInstalled(controls);
		}

		InstallControlsClassAnalysis classAnalysis = InstallControlsClassAnalysis.of(objectType);
		installControls(object, classAnalysis);

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).afterAllControlsInstalled(controls);
		}

		classAnalysis.getNestedInstallControlsFields().forEach(field -> installNestedControls(object, field));
	}

	private void installNestedControls(Object object, Field field) {
		try {
			field.setAccessible(true);
			Object target = field.get(object);
			checkFieldValueNotNull(target, field);
			installControls(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			String msg = "Error accessing nested @InstallControls field %s of %s.";
			msg = String.format(msg, field.getName(), field.getDeclaringClass());
			throw new ReflectionException(msg, e);
		}
	}

	private void warnNonEdtInstallation(Object object) {
		boolean runsOnEdt = SwingUtilities.isEventDispatchThread();
		if (!runsOnEdt) {
			log.warn("Installing controls to " + object
					+ " outside the event dispatch thread. All controls should be installed in the event dispatch thread.");
		}
	}

	private void installControls(Object object, InstallControlsClassAnalysis classAnalysis) {
		//@formatter:off
		classAnalysis.getControlDeclarations().values().stream()
				.map(controlDeclaration-> new ObjectOwnedControlDeclaration(object, controlDeclaration))
				.forEach(this::install);
		//@formatter:on
	}

	private void install(ObjectOwnedControlDeclaration declaration) {
		AnnotationInstaller installer = AnnotationInstallerFactory.get(declaration.getInstallerType());

		ControlDeclarationPerformer controlPerformer = new ControlDeclarationPerformer(controls, declaration);
		Object target = declaration.getTargetObject();

		//A null value occurs only in annotated fields
		if (declaration.getTargetElement() instanceof Field)
			checkFieldValueNotNull(target, (Field) declaration.getTargetElement());

		installer.installAnnotation(declaration.getAnnotation(), target, controlPerformer::perform);
	}

	private void checkFieldValueNotNull(Object target, Field targetField) {
		if (target == null) {
			throw new NullPointerException("Value of field '" + targetField.getName() + "' declared in class "
					+ targetField.getDeclaringClass().getSimpleName() + " is null.");
		}
	}

	private boolean alreadyInstalled(Object object) {
		return installedObjects.containsKey(object);
	}

}

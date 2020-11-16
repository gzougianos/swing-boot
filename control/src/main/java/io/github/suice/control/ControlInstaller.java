package io.github.suice.control;

import static java.util.Objects.requireNonNull;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.control.annotation.installer.AnnotationToComponentInstaller;
import io.github.suice.control.annotation.installer.KeyBindingInstaller;
import io.github.suice.control.annotation.installer.OnActionPerformedInstaller;
import io.github.suice.control.annotation.installer.OnComponentResizedInstaller;

public class ControlInstaller {
	private static final Logger log = LoggerFactory.getLogger(ControlInstaller.class);
	private Set<AnnotationToComponentInstaller> installers = new HashSet<>();
	private final WeakHashMap<Object, Void> installedObjects = new WeakHashMap<>();

	private Controls controls;

	@Inject
	public ControlInstaller(Controls controls) {
		this.controls = controls;
		createDefaultInstallers();
	}

	private void createDefaultInstallers() {
		installers.add(new OnActionPerformedInstaller());
		installers.add(new OnComponentResizedInstaller());
		installers.add(new KeyBindingInstaller());
	}

	public void installControls(Object object) {
		requireNonNull(object, "Cannot install controls to null object.");

		if (alreadyInstalled(object))
			return;

		warnNonEdtInstallation(object);

		installedObjects.put(object, null);

		Class<?> objectType = object.getClass();

		installControls(object, InstallControlsClassAnalysis.of(objectType));

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).installAdditionalControls(controls);
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
		for (AnnotationToComponentInstaller installer : installers) {
			if (!installer.supportsAnnotation(declaration.getAnnotation()))
				continue;

			ControlDeclarationPerformer controlPerformer = new ControlDeclarationPerformer(controls, declaration);
			Component targetComponent = declaration.getTargetComponent();

			if (targetComponent == null) { //Can happen only to fields
				Field targetField = (Field) declaration.getTargetElement();
				throw new NullPointerException("Component value of field '" + targetField.getName() + "' declared in class "
						+ targetField.getDeclaringClass().getSimpleName() + " is null.");
			}

			installer.installAnnotation(declaration.getAnnotation(), targetComponent, controlPerformer::perform);
		}
	}

	private boolean alreadyInstalled(Object object) {
		return installedObjects.containsKey(object);
	}

}

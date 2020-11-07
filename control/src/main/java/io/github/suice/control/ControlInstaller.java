package io.github.suice.control;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.github.suice.control.annotation.installer.AnnotationToComponentInstaller;
import io.github.suice.control.annotation.installer.KeyBindingInstaller;
import io.github.suice.control.annotation.installer.OnActionPerformedInstaller;
import io.github.suice.control.annotation.installer.OnComponentResizedInstaller;

public class ControlInstaller {
	private Set<AnnotationToComponentInstaller> installers = new HashSet<>();
	private final Set<Object> installedObjects = new HashSet<>();

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
		if (alreadyInstalled(object))
			return;

		installedObjects.add(object);

		Class<?> objectType = object.getClass();

		installControls(object, InstallControlsClassAnalysis.of(objectType));

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).installAdditionalControls(controls);
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

			if (targetComponent == null)
				throw new NullPointerException("Component of " + declaration.getTargetComponent() + "is null.");

			installer.installAnnotation(targetComponent, declaration.getAnnotation(), controlPerformer::perform);
		}
	}

	private boolean alreadyInstalled(Object object) {
		return installedObjects.contains(object);
	}

}

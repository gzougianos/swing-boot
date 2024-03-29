package io.github.swingboot.control.installation;

import static java.util.Objects.requireNonNull;

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

import io.github.swingboot.control.AdditionalControlInstallation;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.declaration.ControlInstallationDeclaration;
import io.github.swingboot.control.declaration.InstallControlsClassAnalysis;
import io.github.swingboot.control.installation.factory.Installation;
import io.github.swingboot.control.installation.factory.InstallationContext;
import io.github.swingboot.control.installation.factory.InstallationFactory;
import io.github.swingboot.control.installation.factory.InstallationFactoryProvider;
import io.github.swingboot.control.reflect.ReflectionException;

public class ControlInstaller {
	private static final Logger log = LoggerFactory.getLogger(ControlInstaller.class);
	private Map<Object, List<Installation>> installationsByObject = new WeakHashMap<>();
	private final Controls controls;

	@Inject
	public ControlInstaller(Controls controls) {
		this.controls = controls;
	}

	public void installTo(Object object) {
		requireNonNull(object, "Cannot install controls to null object.");

		if (installedTo(object)) {
			return;
		}

		warnNonEdtInstallation(object);

		Class<?> objectType = object.getClass();

		InstallControlsClassAnalysis classAnalysis = InstallControlsClassAnalysis.of(objectType);

		classAnalysis.getInitializedByDeclaration().ifPresent(declaration -> {
			new ControlPerformer(controls, declaration, object).perform(null);
		});

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).beforeAnyControlInstalled(controls);
		}

		createInstallationsAndInstall(object, classAnalysis);

		if (object instanceof AdditionalControlInstallation) {
			((AdditionalControlInstallation) object).afterAllControlsInstalled(controls);
		}

		classAnalysis.getNestedInstallControlsFields().forEach(field -> installNestedControls(object, field));
	}

	public void uninstallFrom(Object obj) {
		getInstallationsOrThrow(obj).forEach(Installation::uninstall);
	}

	public void reinstallTo(Object obj) {
		getInstallationsOrThrow(obj).forEach(Installation::install);
	}

	public boolean installedTo(Object obj) {
		return installationsByObject.containsKey(obj);
	}

	public boolean areAllInstalledTo(Object obj) {
		return getInstallationsOrThrow(obj).stream().allMatch(Installation::isInstalled);
	}

	public boolean areAllUninstalledFrom(Object obj) {
		return getInstallationsOrThrow(obj).stream().allMatch(t -> !t.isInstalled());
	}

	private List<Installation> getInstallationsOrThrow(Object obj) {
		List<Installation> list = installationsByObject.get(obj);
		if (list == null)
			throw new ControlsWereNeverInstalledException("Controls were never installed to object: " + obj);

		return list;
	}

	private void installNestedControls(Object object, Field field) {
		try {
			if (!field.isAccessible())
				field.setAccessible(true);
			Object target = field.get(object);

			checkFieldValueNotNull(target, field);

			installTo(target);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			String msg = "Error accessing nested @InstallControls field %s of %s.";
			msg = String.format(msg, field.getName(), field.getDeclaringClass());
			throw new ReflectionException(msg, e);
		}
	}

	private void createInstallationsAndInstall(Object owner, InstallControlsClassAnalysis classAnalysis) {
		//@formatter:off
		List<Installation> installations = classAnalysis.getControlDeclarations()
				.values().stream()
				.map(declaration->createInstallation(owner, declaration))
				.collect(Collectors.toList());
		//@formatter:on

		installationsByObject.put(owner, Collections.unmodifiableList(installations));
		reinstallTo(owner); //actually the first installation
	}

	private Installation createInstallation(Object owner, ControlInstallationDeclaration declaration) {
		InstallationFactory installationFactory = InstallationFactoryProvider
				.get(declaration.getInstallerType());

		ControlPerformer controlPerformer = new ControlPerformer(controls, declaration, owner);

		final Object target = declaration.getInstallationTargetFor(owner);

		InstallationContext context = new InstallationContext(owner, target, declaration.getAnnotation(),
				controlPerformer::perform);

		return installationFactory.create(context);
	}

	private void checkFieldValueNotNull(Object value, Field field) {
		if (value == null) {
			throw new NullPointerException("Value of field '" + field.getName() + "' declared in class "
					+ field.getDeclaringClass().getSimpleName() + " is null.");
		}
	}

	private void warnNonEdtInstallation(Object object) {
		boolean runsOnEdt = SwingUtilities.isEventDispatchThread();
		if (!runsOnEdt) {
			log.warn("Installing controls to " + object
					+ " outside the event dispatch thread. All controls should be installed in the event dispatch thread.");
		}
	}

	public static class ControlsWereNeverInstalledException extends RuntimeException {
		private static final long serialVersionUID = -420040964800757920L;

		private ControlsWereNeverInstalledException(String message) {
			super(message);
		}

	}
}

package io.github.suice.command;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.github.suice.command.annotation.installer.AnnotationToComponentInstaller;
import io.github.suice.command.annotation.installer.OnActionPerformedInstaller;
import io.github.suice.command.annotation.installer.OnComponentResizedInstaller;

public class CommandInstaller {
	private static final Map<Class<?>, InstallCommandsClassAnalysis> scanCache = new HashMap<>();
	private Set<AnnotationToComponentInstaller> installers = new HashSet<>();
	private final Set<Object> installedObjects = new HashSet<>();

	private CommandExecutor executor;

	@Inject
	public CommandInstaller(CommandExecutor executor) {
		this.executor = executor;
		createDefaultInstallers();
	}

	private void createDefaultInstallers() {
		installers.add(new OnActionPerformedInstaller());
		installers.add(new OnComponentResizedInstaller());
	}

	public void installCommands(Object object) {
		if (alreadyInstalled(object))
			return;

		installedObjects.add(object);

		Class<?> objectType = object.getClass();

		InstallCommandsClassAnalysis classAnalysis = fromCacheOrNew(objectType);

		installCommandsOnFields(object, classAnalysis);

		if (object instanceof AdditionalCommandInstallation) {
			((AdditionalCommandInstallation) object).installCommands(executor);
		}

	}

	private void installCommandsOnFields(Object object, InstallCommandsClassAnalysis classAnalysis) {
		//@formatter:off
		classAnalysis.getCommandDeclarations().values().stream()
				.map(cmdDeclaration-> new ObjectOwnedCommandDeclaration(object, cmdDeclaration))
				.forEach(this::install);
		//@formatter:on
	}

	private void install(ObjectOwnedCommandDeclaration declaration) {
		for (AnnotationToComponentInstaller installer : installers) {
			if (!installer.supportsAnnotation(declaration.getAnnotation()))
				continue;

			CommandDeclarationExecutor cmdDeclarationExecutor = new CommandDeclarationExecutor(executor, declaration);
			Component targetComponent = declaration.getTargetComponent();

			if (targetComponent == null)
				throw new NullPointerException("Component of " + declaration.getTargetComponent() + "is null.");

			installer.installAnnotation(targetComponent, declaration.getAnnotation(), cmdDeclarationExecutor::execute);
		}
	}

	private InstallCommandsClassAnalysis fromCacheOrNew(Class<?> objectType) {
		InstallCommandsClassAnalysis classScan = scanCache.get(objectType);
		if (classScan == null) {
			classScan = new InstallCommandsClassAnalysis(objectType);
			scanCache.put(objectType, classScan);
		}
		return classScan;
	}

	private boolean alreadyInstalled(Object object) {
		return installedObjects.contains(object);
	}

}

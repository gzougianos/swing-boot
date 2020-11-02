package io.github.suice.command;

import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.OnComponentResized;
import io.github.suice.command.annotation.installer.CommandDeclarationInstaller;
import io.github.suice.command.annotation.installer.ListenerDirectlyToComponentInstaller;
import io.github.suice.command.annotation.installer.creator.OnActionPerformedCreator;
import io.github.suice.command.annotation.installer.creator.OnComponentResizedCreator;

public class CommandInstaller {
	private static final Map<Class<?>, InstallCommandsClassAnalysis> scanCache = new HashMap<>();
	private Set<CommandDeclarationInstaller> declarationInstallers = new HashSet<>();
	private final Set<Object> installedObjects = new HashSet<>();

	private CommandExecutor executor;

	@Inject
	public CommandInstaller(CommandExecutor executor) {
		this.executor = executor;
		createDefaultInstallers();
	}

	private void createDefaultInstallers() {
		//@formatter:off
		declarationInstallers.add(
				new ListenerDirectlyToComponentInstaller<>(
						executor,
						OnActionPerformed.class, 
						"addActionListener",
						ActionListener.class, 
						new OnActionPerformedCreator()
						));
		
		declarationInstallers.add(
				new ListenerDirectlyToComponentInstaller<>(
						executor,
						OnComponentResized.class, 
						"addComponentListener",
						ComponentListener.class, 
						new OnComponentResizedCreator()
						));
		//@formatter:on
	}

	public void addCommandDeclarationInstaller(CommandDeclarationInstaller installer) {
		declarationInstallers.add(installer);
	}

	public void installCommands(Object object) throws CommandInstallationException {
		if (alreadyInstalled(object))
			throw new CommandInstallationException(object + " is already initialized.");

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
				.forEach(cmdDeclaration -> {
					declarationInstallers.stream()
						.filter(installer -> installer.supports(cmdDeclaration))		
						.forEach(installer -> installer.install(new ObjectOwnedCommandDeclaration(object, cmdDeclaration)));
				});
		//@formatter:on
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

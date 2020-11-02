package io.github.suice.command;

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class CommandModule extends AbstractModule {
	private static final Logger log = LoggerFactory.getLogger(CommandModule.class);
	private static final Matcher<TypeLiteral<?>> INITIALIZE_COMMANDS_MATCHER = new InstallCommandsMatcher();
	private ClassPath classpath;
	private final String commandPackage;
	private CommandInstallerInjectionListener commandInstallerInjectionListener;
	private boolean includeSubpackages;

	public CommandModule(String commandPackage) {
		this(commandPackage, false);
	}

	public CommandModule(Class<? extends Command<?>> classInCommandPackage, boolean includeSubpackages) {
		this(classInCommandPackage.getPackage().getName(), includeSubpackages);
	}

	public CommandModule(Class<? extends Command<?>> classInCommandPackage) {
		this(classInCommandPackage, false);
	}

	public CommandModule(String commandPackage, boolean includeSubpackages) {
		this.commandPackage = commandPackage;
		this.includeSubpackages = includeSubpackages;
		this.commandInstallerInjectionListener = new CommandInstallerInjectionListener();
	}

	@Override
	protected void configure() {
		initClassPath();
		requestInjection(this);
		bindListener(INITIALIZE_COMMANDS_MATCHER, new TypeListener() {

			@Override
			public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
				encounter.register(commandInstallerInjectionListener);
			}
		});

		bind(CommandExecutor.class).to(DefaultInjectableCommandExecutor.class).asEagerSingleton();
		bind(CommandInstaller.class).asEagerSingleton();

		bindCommands();

	}

	private void bindCommands() {
		ImmutableSet<ClassInfo> topLevelClasses = includeSubpackages ? classpath.getTopLevelClassesRecursive(commandPackage)
				: classpath.getTopLevelClasses(commandPackage);

		for (ClassInfo classInfo : topLevelClasses) {
			Class<?> clazz = classInfo.load();
			if (isCommand(clazz))
				bind(clazz);
		}
	}

	private boolean isCommand(Class<?> clazz) {
		//@formatter:off
		return Command.class.isAssignableFrom(clazz) 
				&& !clazz.equals(Command.class)
				&& !Modifier.isAbstract(clazz.getModifiers()) 
				&& !clazz.isAnonymousClass();
		//@formatter:on
	}

	@Inject
	private void initListener(CommandInstaller commandInstaller, Injector injector) {
		commandInstallerInjectionListener.setCommandInstaller(commandInstaller);
		injector.getAllBindings().forEach((k, b) -> {
			System.out.println(k);
		});
	}

	private void initClassPath() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			classpath = ClassPath.from(loader);
		} catch (IOException e) {
			log.error("Error creating classpath.", e);
		}
	}
}

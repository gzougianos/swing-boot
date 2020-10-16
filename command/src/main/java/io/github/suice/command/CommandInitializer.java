package io.github.suice.command;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.github.suice.command.annotation.installer.CommandAnnotationInstaller;
import io.github.suice.command.annotation.installer.OnActionPerformedInstaller;

public class CommandInitializer {
	private final Set<Object> initializedObjects = new HashSet<>();
	private final Set<CommandAnnotationInstaller> installers = new HashSet<>();
	private CommandExecutor executor;

	@Inject
	public CommandInitializer(CommandExecutor executor) {
		this.executor = executor;
		createDefaultInstallers();
	}

	private void createDefaultInstallers() {
		installers.add(new OnActionPerformedInstaller(executor));
	}

	public void addAnnotationInstaller(CommandAnnotationInstaller installer) {
		installers.add(installer);
	}

	public void initializeCommands(Object object) throws CommandInitializationException {
		if (alreadyInitialized(object))
			throw new CommandInitializationException(object + " is already initialized.");

		initializedObjects.add(object);

		Class<?> objectType = object.getClass();
		Set<Field> fields = ReflectionSupport.getDeclaredAndInheritedFields(objectType,
				InitializeCommands.class);

		for (Field field : fields) {
			if (!isComponentField(field))
				continue;

			Annotation[] annotations = field.getDeclaredAnnotations();
			if (annotations.length == 0)
				continue;

			ensureAccessible(field);

			try {
				Component component = (Component) field.get(object);
				ensureNotNull(component, field);

				for (Annotation annotation : annotations) {
					installers.stream().filter(i -> i.supportsAnnotation(annotation))
							.forEach(i -> i.installAnnotation(component, annotation));
				}

			} catch (IllegalAccessException e) {
				throw new CommandInitializationException("Cannot get acccess to field " + field + ".");
			}
		}

	}

	private void ensureNotNull(Component component, Field field) {
		if (component == null) {
			throw new CommandInitializationException(field.getType().getSimpleName() + " field `" + field.getName()
					+ "` of class " + field.getDeclaringClass().getSimpleName() + " is null.", new NullPointerException());
		}
	}

	private void ensureAccessible(Field field) {
		if (!field.isAccessible())
			field.setAccessible(true);
	}

	private boolean isComponentField(Field field) {
		return Component.class.isAssignableFrom(field.getType());
	}

	private boolean alreadyInitialized(Object object) {
		return initializedObjects.contains(object);
	}

}

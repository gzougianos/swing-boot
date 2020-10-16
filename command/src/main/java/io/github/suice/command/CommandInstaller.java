package io.github.suice.command;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.github.suice.command.annotation.installer.ComponentAnnotationResolver;
import io.github.suice.command.annotation.installer.OnActionPerformedResolver;

public class CommandInstaller {
	private final Set<Object> installedObjects = new HashSet<>();
	private final Set<ComponentAnnotationResolver> annotationResolvers = new HashSet<>();
	private CommandExecutor executor;

	@Inject
	public CommandInstaller(CommandExecutor executor) {
		this.executor = executor;
		createDefaultInstallers();
	}

	private void createDefaultInstallers() {
		annotationResolvers.add(new OnActionPerformedResolver(executor));
	}

	public void addAnnotationResolver(ComponentAnnotationResolver resolver) {
		annotationResolvers.add(resolver);
	}

	public void installCommands(Object object) throws CommandInstallationException {
		if (alreadyInstalled(object))
			throw new CommandInstallationException(object + " is already initialized.");

		installedObjects.add(object);

		installCommandsOnFields(object);

		if (object instanceof ParameterizedCommandInstaller) {
			((ParameterizedCommandInstaller) object).installCommands(executor);
		}

	}

	private void installCommandsOnFields(Object object) {
		Class<?> objectType = object.getClass();
		Set<Field> fields = ReflectionSupport.getDeclaredAndInheritedFields(objectType, InstallCommands.class);

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
					annotationResolvers.stream().filter(i -> i.supports(annotation))
							.forEach(i -> i.install(component, annotation));
				}

			} catch (IllegalAccessException e) {
				throw new CommandInstallationException("Cannot get acccess to field " + field + ".");
			}
		}
	}

	private void ensureNotNull(Component component, Field field) {
		if (component == null) {
			throw new CommandInstallationException(field.getType().getSimpleName() + " field `" + field.getName() + "` of class "
					+ field.getDeclaringClass().getSimpleName() + " is null.", new NullPointerException());
		}
	}

	private void ensureAccessible(Field field) {
		if (!field.isAccessible())
			field.setAccessible(true);
	}

	private boolean isComponentField(Field field) {
		return Component.class.isAssignableFrom(field.getType());
	}

	private boolean alreadyInstalled(Object object) {
		return installedObjects.contains(object);
	}

}

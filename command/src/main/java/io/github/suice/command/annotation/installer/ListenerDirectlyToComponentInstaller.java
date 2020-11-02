package io.github.suice.command.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.command.CommandDeclaration;
import io.github.suice.command.CommandDeclarationExecutor;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.ObjectOwnedCommandDeclaration;
import io.github.suice.command.annotation.installer.creator.ListenerCreator;

public class ListenerDirectlyToComponentInstaller<A extends Annotation, T extends EventListener>
		implements CommandDeclarationInstaller {
	private static final Logger log = LoggerFactory.getLogger(ListenerDirectlyToComponentInstaller.class);
	private CommandExecutor executor;
	private Class<A> annotationtype;
	private String addMethod;
	private Class<T> listenerType;
	private ListenerCreator<A, T> listenerCreator;

	public ListenerDirectlyToComponentInstaller(CommandExecutor executor, Class<A> annotationtype, String addMethod,
			Class<T> listenerType, ListenerCreator<A, T> listenerCreator) {
		this.executor = executor;
		this.annotationtype = annotationtype;
		this.addMethod = addMethod;
		this.listenerType = listenerType;
		this.listenerCreator = listenerCreator;
	}

	@Override
	public boolean supports(CommandDeclaration declaration) {
		return declaration.getAnnotation().annotationType().equals(annotationtype);
	}

	private void ensureAccessible(Field targetField) {
		if (!targetField.isAccessible())
			targetField.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void install(ObjectOwnedCommandDeclaration cmdDeclaration) {
		try {
			Component component = getTargetComponent(cmdDeclaration);

			CommandDeclarationExecutor cmdDeclarationExecutor = new CommandDeclarationExecutor(executor, cmdDeclaration);
			Consumer<AWTEvent> eventConsumer = cmdDeclarationExecutor::execute;
			T listener = listenerCreator.createListener((A) cmdDeclaration.getAnnotation(), eventConsumer);
			Class<? extends Component> componentType = component.getClass();
			componentType.getMethod(addMethod, listenerType).invoke(component, listener);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			log.error("Error installing command declaration " + cmdDeclaration, e);
		}
	}

	private Component getTargetComponent(ObjectOwnedCommandDeclaration cmdDeclaration) throws IllegalAccessException {
		Component component;
		if (cmdDeclaration.getTargetElement() instanceof Field) {
			Field targetField = (Field) cmdDeclaration.getTargetElement();
			ensureAccessible(targetField);
			component = (Component) targetField.get(cmdDeclaration.getOwner());
			ensureNotNull(component, targetField);
		} else {
			component = (Component) cmdDeclaration.getOwner();
		}
		return component;
	}

	private void ensureNotNull(Component component, Field targetField) {
		if (component == null)
			throw new RuntimeException(targetField + " of class" + targetField.getDeclaringClass().getSimpleName() + " is null.");

	}

}

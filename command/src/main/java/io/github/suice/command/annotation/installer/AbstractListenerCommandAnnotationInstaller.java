package io.github.suice.command.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.ReflectionSupport;

abstract class AbstractListenerAnnotationInstaller<A extends Annotation, T extends EventListener>
		implements CommandAnnotationInstaller {
	private static final Logger log = LoggerFactory.getLogger(AbstractListenerAnnotationInstaller.class);
	private final Class<A> annotationType;
	private final String addMethod;
	private final Class<T> listenerType;
	private CommandExecutor executor;

	public AbstractListenerAnnotationInstaller(CommandExecutor executor, Class<A> annotationtype, String addMethod,
			Class<T> listenerType) {
		this.executor = executor;
		this.annotationType = annotationtype;
		this.addMethod = addMethod;
		this.listenerType = listenerType;
	}

	@Override
	public final void installAnnotation(Component component, Annotation annotationObj) {
		if (!supportsComponent(component))
			throw new IllegalArgumentException(
					getClass().getSimpleName() + " supports only components with " + addMethod + " method.");

		@SuppressWarnings("unchecked")
		T listener = createListener((A) annotationObj);

		Class<? extends Component> componentType = component.getClass();
		try {
			componentType.getMethod(addMethod, listenerType).invoke(component, listener);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("Error invoking " + addMethod + " method to " + componentType, e);
		}
	}

	abstract T createListener(A annotation);

	@Override
	public final boolean supportsAnnotation(Annotation annotation) {
		return annotationType.equals(annotation.annotationType());
	}

	private final boolean supportsComponent(Component component) {
		Class<?> componentType = component.getClass();
		return ReflectionSupport.hasMethod(addMethod, componentType);
	}

	@SuppressWarnings("unchecked")
	protected void executeConsideringEventParameterized(
			Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> commandTypesWithParameterTypes, AWTEvent event) {
		for (Class<? extends Command<?>> cmdType : commandTypesWithParameterTypes.keySet()) {
			Class<? extends AWTEvent> parType = commandTypesWithParameterTypes.get(cmdType);
			Class<? extends Command<AWTEvent>> parametrizedCmdType = (Class<? extends Command<AWTEvent>>) cmdType;
			if (parType != null && event.getClass().isAssignableFrom(parType))
				executor.execute(parametrizedCmdType, event);
			else
				executor.execute(cmdType);
		}
	}

}
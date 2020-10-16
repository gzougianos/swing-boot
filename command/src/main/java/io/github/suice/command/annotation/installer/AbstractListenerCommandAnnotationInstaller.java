package io.github.suice.command.annotation.installer;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.command.ReflectionSupport;

abstract class AbstractListenerAnnotationResolver<A extends Annotation, T extends EventListener>
		implements ComponentAnnotationResolver {
	private static final Logger log = LoggerFactory.getLogger(AbstractListenerAnnotationResolver.class);
	private final Class<A> annotationType;
	private final String addMethod;
	private final Class<T> listenerType;

	public AbstractListenerAnnotationResolver(Class<A> annotationtype, String addMethod, Class<T> listenerType) {
		this.annotationType = annotationtype;
		this.addMethod = addMethod;
		this.listenerType = listenerType;
	}

	@Override
	public final void install(Component component, Annotation annotationObj) {
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
	public final boolean supports(Annotation annotation) {
		return annotationType.equals(annotation.annotationType());
	}

	private final boolean supportsComponent(Component component) {
		Class<?> componentType = component.getClass();
		return ReflectionSupport.hasMethod(addMethod, componentType);
	}

}
package io.github.swingboot.control.installation.factory;

import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

public class InstallationContext {

	private Object owner;
	private Object target;
	private Annotation annotation;
	private Consumer<EventObject> eventConsumer;

	public InstallationContext(Object owner, Object target, Annotation annotation,
			Consumer<EventObject> eventConsumer) {
		this.owner = owner;
		this.target = target;
		this.annotation = annotation;
		this.eventConsumer = eventConsumer;
	}

	public Object getOwner() {
		return owner;
	}

	public Object getTarget() {
		return target;
	}

	public <T extends Annotation> T getAnnotationAs(Class<T> annotationType) {
		return annotationType.cast(annotation);
	}

	public Consumer<EventObject> getEventConsumer() {
		return eventConsumer;
	}

}

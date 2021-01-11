package io.github.swingboot.control.annotation.installer;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

import io.github.swingboot.control.listener.ControlListener;

public class OnComponentResizedInstaller implements AnnotationInstaller {

	@Override
	public void installAnnotation(Annotation annotation, Object target, Consumer<EventObject> eventConsumer) {
		if (target instanceof Component)
			((Component) target).addComponentListener(new Listener(eventConsumer));
		else
			throw new UnsupportedOperationException(
					"@OnComponentResized cannot be installed to target of type " + target.getClass());
	}

	private static class Listener extends ComponentAdapter implements ControlListener {
		private Consumer<EventObject> eventConsumer;

		public Listener(Consumer<EventObject> eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			eventConsumer.accept(e);
		}
	}
}

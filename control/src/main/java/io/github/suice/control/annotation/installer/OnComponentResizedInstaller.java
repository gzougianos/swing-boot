package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.suice.control.annotation.OnComponentResized;
import io.github.suice.control.listener.ControlListener;

public class OnComponentResizedInstaller implements AnnotationToComponentInstaller {

	@Override
	public boolean supportsAnnotation(Annotation annotation) {
		return annotation.annotationType().equals(OnComponentResized.class);
	}

	@Override
	public void installAnnotation(Annotation annotation, Component component, Consumer<AWTEvent> eventConsumer) {
		component.addComponentListener(new Listener(eventConsumer));
	}

	private static class Listener extends ComponentAdapter implements ControlListener {
		private Consumer<AWTEvent> eventConsumer;

		public Listener(Consumer<AWTEvent> eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			eventConsumer.accept(e);
		}
	}
}

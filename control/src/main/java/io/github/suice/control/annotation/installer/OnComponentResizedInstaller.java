package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.suice.control.listener.ControlListener;

public class OnComponentResizedInstaller implements AnnotationInstaller {

	@Override
	public void installAnnotation(Annotation annotation, Object target, Consumer<AWTEvent> eventConsumer) {
		if (target instanceof Component)
			((Component) target).addComponentListener(new Listener(eventConsumer));
		else
			throw new UnsupportedOperationException(
					"@OnComponentResized cannot be installed to target of type " + target.getClass());
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

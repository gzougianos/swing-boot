package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

public class OnComponentResizedInstallationFactory implements ControlInstallationFactory {
	OnComponentResizedInstallationFactory() {
	}

	@Override
	public ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer) {
		final Component component;
		if (target instanceof Component) {
			component = (Component) target;
		} else
			throw new UnsupportedOperationException(
					"@OnComponentResized cannot be installed to target of type " + target.getClass());

		final Listener listener = new Listener(eventConsumer);

		return new ControlInstallation(() -> {
			component.addComponentListener(listener);
		}, () -> {
			component.removeComponentListener(listener);
		});
	}

	private static class Listener extends ComponentAdapter {
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

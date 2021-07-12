package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class OnComponentResizedInstallationFactory implements InstallationFactory {
	OnComponentResizedInstallationFactory() {
	}

	@Override
	public Installation create(InstallationContext context) {
		final Component component = (Component) context.getTarget();
		final ComponentListener listener = createListener(context);

		return CommonInstallations.component(component, listener);
	}

	private ComponentListener createListener(InstallationContext context) {
		final ComponentListener listener = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				context.getEventConsumer().accept(e);
			}
		};
		return listener;
	}

}

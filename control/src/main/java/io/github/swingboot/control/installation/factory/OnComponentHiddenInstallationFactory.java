package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class OnComponentHiddenInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		final Component target = (Component) context.getTarget();

		final ComponentListener listener = new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				context.getEventConsumer().accept(e);
			}
		};

		return new Installation(() -> {
			target.addComponentListener(listener);
		}, () -> {
			target.removeComponentListener(listener);
		});
	}

}

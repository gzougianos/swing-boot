package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class OnComponentMovedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();

		ComponentListener listener = new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.component(target, listener);
	}

}

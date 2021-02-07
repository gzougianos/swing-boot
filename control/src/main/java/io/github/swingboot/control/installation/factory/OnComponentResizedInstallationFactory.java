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
		Object target = context.getTarget();
		if (!(target instanceof Component)) {
			throw new UnsupportedOperationException(
					"@OnComponentResized cannot be installed to target of type " + target.getClass());
		}

		final Component component = (Component) target;
		final ComponentListener listener = createListener(context);

		return new Installation(() -> {
			component.addComponentListener(listener);
		}, () -> {
			component.removeComponentListener(listener);
		});
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

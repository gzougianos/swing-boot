package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class OnFocusLostInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();

		FocusListener listener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				context.getEventConsumer().accept(e);
			}
		};

		return new Installation(() -> {
			target.addFocusListener(listener);
		}, () -> {
			target.removeFocusListener(listener);
		});
	}

}

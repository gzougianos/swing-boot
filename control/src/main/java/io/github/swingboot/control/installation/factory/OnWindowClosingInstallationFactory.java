package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowClosing;

public class OnWindowClosingInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowClosing onWindowClosing = context.getAnnotationAs(OnWindowClosing.class);
		Window window = (Window) context.getTarget();

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				boolean validOldState = onWindowClosing.oldState().matches(e.getOldState());
				boolean validNewState = onWindowClosing.newState().matches(e.getNewState());
				if (validOldState && validNewState)
					context.getEventConsumer().accept(e);
			};
		};

		return new Installation(() -> {
			window.addWindowListener(listener);
		}, () -> {
			window.removeWindowListener(listener);
		});
	}
}

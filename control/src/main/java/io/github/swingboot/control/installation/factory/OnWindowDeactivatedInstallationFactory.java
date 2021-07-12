package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowDeactivated;

public class OnWindowDeactivatedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowDeactivated onWindowDeactivated = context.getAnnotationAs(OnWindowDeactivated.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowDeactivated.oldState(),
				onWindowDeactivated.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.window(window, listener);
	}
}

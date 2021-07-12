package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowClosed;

public class OnWindowClosedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowClosed onWindowClosed = context.getAnnotationAs(OnWindowClosed.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowClosed.oldState(),
				onWindowClosed.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			};
		};

		return CommonInstallations.window(window, listener);
	}
}

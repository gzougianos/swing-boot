package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowOpened;

public class OnWindowOpenedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowOpened onWindowOpened = context.getAnnotationAs(OnWindowOpened.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowOpened.oldState(),
				onWindowOpened.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.window(window, listener);
	}
}

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

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowClosing.oldState(),
				onWindowClosing.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			};
		};

		return CommonInstallations.window(window, listener);
	}
}

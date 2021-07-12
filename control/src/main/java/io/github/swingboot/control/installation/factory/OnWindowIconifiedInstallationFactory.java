package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowIconified;

public class OnWindowIconifiedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowIconified onWindowIconified = context.getAnnotationAs(OnWindowIconified.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowIconified.oldState(),
				onWindowIconified.newState());

		final WindowListener listener = new WindowAdapter() {

			@Override
			public void windowIconified(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.window(window, listener);
	}
}

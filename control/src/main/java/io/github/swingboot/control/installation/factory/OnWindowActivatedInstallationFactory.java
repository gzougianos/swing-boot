package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowActivated;

public class OnWindowActivatedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowActivated onWindowActivated = context.getAnnotationAs(OnWindowActivated.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowActivated.oldState(),
				onWindowActivated.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			};
		};

		return CommonInstallations.window(window, listener);
	}
}

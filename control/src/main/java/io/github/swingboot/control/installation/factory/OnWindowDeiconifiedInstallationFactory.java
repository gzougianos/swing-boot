package io.github.swingboot.control.installation.factory;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import io.github.swingboot.control.installation.annotation.OnWindowDeiconified;

public class OnWindowDeiconifiedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		OnWindowDeiconified onWindowDeiconified = context.getAnnotationAs(OnWindowDeiconified.class);
		Window window = (Window) context.getTarget();

		WindowEventPredicate predicate = new WindowEventPredicate(onWindowDeiconified.oldState(),
				onWindowDeiconified.newState());

		final WindowListener listener = new WindowAdapter() {
			@Override
			public void windowDeiconified(WindowEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.window(window, listener);
	}
}

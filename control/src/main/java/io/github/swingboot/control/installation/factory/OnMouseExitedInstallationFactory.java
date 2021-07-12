package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.github.swingboot.control.installation.annotation.OnMouseExited;

public class OnMouseExitedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseExited onMouseExited = context.getAnnotationAs(OnMouseExited.class);

		final int modifiers = onMouseExited.modifiers();
		final int button = onMouseExited.button();
		final int clickCount = onMouseExited.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return new Installation(() -> {
			target.addMouseListener(listener);
		}, () -> {
			target.removeMouseListener(listener);
		});
	}

}

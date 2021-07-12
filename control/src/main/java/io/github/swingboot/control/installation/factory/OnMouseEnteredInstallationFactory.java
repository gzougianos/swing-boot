package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.github.swingboot.control.installation.annotation.OnMouseEntered;

public class OnMouseEnteredInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseEntered onMouseEntered = context.getAnnotationAs(OnMouseEntered.class);

		final int modifiers = onMouseEntered.modifiers();
		final int button = onMouseEntered.button();
		final int clickCount = onMouseEntered.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
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

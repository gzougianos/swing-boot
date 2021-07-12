package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.github.swingboot.control.installation.annotation.OnMousePressed;

public class OnMousePressedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMousePressed onMousePressed = context.getAnnotationAs(OnMousePressed.class);

		final int modifiers = onMousePressed.modifiers();
		final int button = onMousePressed.button();
		final int clickCount = onMousePressed.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.mouse(target, listener);
	}

}

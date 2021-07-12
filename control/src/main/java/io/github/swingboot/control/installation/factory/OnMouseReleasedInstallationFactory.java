package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.github.swingboot.control.installation.annotation.OnMouseReleased;

public class OnMouseReleasedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseReleased onMouseReleased = context.getAnnotationAs(OnMouseReleased.class);

		final int modifiers = onMouseReleased.modifiers();
		final int button = onMouseReleased.button();
		final int clickCount = onMouseReleased.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return CommonInstallations.mouse(target, listener);
	}

}

package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import io.github.swingboot.control.installation.annotation.OnMouseMoved;

public class OnMouseMovedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseMoved onMouseMoved = context.getAnnotationAs(OnMouseMoved.class);

		final int modifiers = onMouseMoved.modifiers();
		final int button = onMouseMoved.button();
		final int clickCount = onMouseMoved.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseMotionListener listener = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (predicate.test(e))
					context.getEventConsumer().accept(e);
			}
		};

		return new Installation(() -> {
			target.addMouseMotionListener(listener);
		}, () -> {
			target.removeMouseMotionListener(listener);
		});
	}

}

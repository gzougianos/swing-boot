package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import io.github.swingboot.control.installation.annotation.OnMouseDragged;

public class OnMouseDraggedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseDragged onMouseDragged = context.getAnnotationAs(OnMouseDragged.class);

		final int modifiers = onMouseDragged.modifiers();
		final int button = onMouseDragged.button();
		final int clickCount = onMouseDragged.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseMotionListener listener = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
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

package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.github.swingboot.control.installation.annotation.OnMouseClicked;

public class OnMouseClickedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnMouseClicked onMouseClicked = context.getAnnotationAs(OnMouseClicked.class);

		final int modifiers = onMouseClicked.modifiers();
		final int button = onMouseClicked.button();
		final int clickCount = onMouseClicked.clickCount();

		final MouseEventPredicate predicate = new MouseEventPredicate(button, clickCount, modifiers);

		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
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

package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import io.github.swingboot.control.installation.annotation.OnKeyReleased;

public class OnKeyReleasedInstallationFactory implements InstallationFactory {
	OnKeyReleasedInstallationFactory() {
	}

	@Override
	public Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		OnKeyReleased annotation = context.getAnnotationAs(OnKeyReleased.class);

		final int annotationModifiers = annotation.modifiers();
		final boolean anyModifier = annotationModifiers == OnKeyReleased.ANY_MODIFIER;

		final int annotationKeyCode = annotation.keyCode();
		final boolean anyKeyCode = annotationKeyCode == OnKeyReleased.ANY_KEY_CODE;

		KeyListener listener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent event) {
				boolean modifiersMatch = anyModifier
						|| ((event.getModifiers() & annotationModifiers) == annotationModifiers);

				boolean keyCodeMatch = anyKeyCode || event.getKeyCode() == annotationKeyCode;
				if (keyCodeMatch && modifiersMatch) {
					context.getEventConsumer().accept(event);
				}
			}
		};

		return new Installation(() -> {
			target.addKeyListener(listener);
		}, () -> {
			target.removeKeyListener(listener);
		});
	}

}

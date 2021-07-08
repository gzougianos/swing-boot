package io.github.swingboot.control.installation.factory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnKeyReleased;

public class OnKeyReleasedInstallationFactory extends OnKeyInstallationFactory {
	OnKeyReleasedInstallationFactory() {
	}

	@Override
	protected KeyListener createListener(Consumer<KeyEvent> eventConsumer) {
		return new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent event) {
				eventConsumer.accept(event);
			}
		};
	}

	@Override
	protected int getAnnotationModifiers(Annotation onKeyAnnotation) {
		return ((OnKeyReleased) onKeyAnnotation).modifiers();
	}

	@Override
	protected int getAnnotationKeyCode(Annotation onKeyAnnotation) {
		return ((OnKeyReleased) onKeyAnnotation).keyCode();
	}

}
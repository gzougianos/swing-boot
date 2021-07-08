package io.github.swingboot.control.installation.factory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnKeyPressed;

public class OnKeyPressedInstallationFactory extends OnKeyInstallationFactory {
	OnKeyPressedInstallationFactory() {
	}

	@Override
	protected KeyListener createListener(Consumer<KeyEvent> eventConsumer) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				eventConsumer.accept(e);
			}
		};
	}

	@Override
	protected int getAnnotationModifiers(Annotation onKeyAnnotation) {
		return ((OnKeyPressed) onKeyAnnotation).modifiers();
	}

	@Override
	protected int getAnnotationKeyCode(Annotation onKeyAnnotation) {
		return ((OnKeyPressed) onKeyAnnotation).keyCode();
	}

}

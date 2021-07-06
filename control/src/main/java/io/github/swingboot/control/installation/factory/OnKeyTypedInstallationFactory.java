package io.github.swingboot.control.installation.factory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnKey.OnKeyTyped;

public class OnKeyTypedInstallationFactory extends OnKeyInstallationFactory {
	OnKeyTypedInstallationFactory() {
	}

	@Override
	protected KeyListener createListener(Consumer<KeyEvent> eventConsumer) {
		return new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				eventConsumer.accept(e);
			}
		};
	}

	@Override
	protected int getAnnotationModifiers(Annotation onKeyAnnotation) {
		return ((OnKeyTyped) onKeyAnnotation).modifiers();
	}

	@Override
	protected int getAnnotationKeyCode(Annotation onKeyAnnotation) {
		return ((OnKeyTyped) onKeyAnnotation).keyCode();
	}

}

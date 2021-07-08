package io.github.swingboot.control.installation.factory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnMouseEntered;

public class OnMouseEnteredInstallationFactory extends OnMouseInstallationFactory {

	@Override
	protected int getAnnotationButton(Annotation annotation) {
		return ((OnMouseEntered) annotation).button();
	}

	@Override
	protected MouseListener createListener(Consumer<MouseEvent> eventConsumer) {
		return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				eventConsumer.accept(e);
			}
		};
	}

	@Override
	protected int getAnnotationModifiers(Annotation onMouseAnnotation) {
		return ((OnMouseEntered) onMouseAnnotation).modifiers();
	}

	@Override
	protected int getAnnotationClickCount(Annotation onMouseAnnotation) {
		return ((OnMouseEntered) onMouseAnnotation).clickCount();
	}

}

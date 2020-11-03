package io.github.suice.command.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.suice.command.annotation.OnComponentResized;

public class OnComponentResizedInstaller implements AnnotationToComponentInstaller {

	@Override
	public boolean supportsAnnotation(Annotation annotation) {
		return annotation.annotationType().equals(OnComponentResized.class);
	}

	@Override
	public void installAnnotation(Component component, Annotation annotation, Consumer<AWTEvent> eventConsumer) {
		component.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				eventConsumer.accept(e);
			}
		});
	}

}

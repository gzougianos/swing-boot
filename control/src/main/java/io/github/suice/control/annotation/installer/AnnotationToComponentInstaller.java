package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

public interface AnnotationToComponentInstaller {

	boolean supportsAnnotation(Annotation annotation);

	void installAnnotation(Annotation annotation, Component component, Consumer<AWTEvent> eventConsumer);
}

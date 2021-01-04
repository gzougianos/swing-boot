package io.github.swingboot.control.annotation.installer;

import java.awt.AWTEvent;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

public interface AnnotationInstaller {

	void installAnnotation(Annotation annotation, Object target, Consumer<AWTEvent> eventConsumer);
}

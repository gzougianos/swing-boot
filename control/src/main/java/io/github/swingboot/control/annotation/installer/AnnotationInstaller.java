package io.github.swingboot.control.annotation.installer;

import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

public interface AnnotationInstaller {

	ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer);
}

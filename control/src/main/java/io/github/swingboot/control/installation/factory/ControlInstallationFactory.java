package io.github.swingboot.control.installation.factory;

import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

public interface ControlInstallationFactory {

	ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer);
}
package io.github.suice.command.annotation.installer;

import java.awt.Component;
import java.lang.annotation.Annotation;

public interface CommandAnnotationInstaller {

	boolean supportsAnnotation(Annotation annotation);

	void installAnnotation(Component component, Annotation annotation);
}

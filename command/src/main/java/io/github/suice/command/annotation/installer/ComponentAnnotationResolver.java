package io.github.suice.command.annotation.installer;

import java.awt.Component;
import java.lang.annotation.Annotation;

public interface ComponentAnnotationResolver {

	boolean supports(Annotation annotation);

	void install(Component component, Annotation annotation);
}

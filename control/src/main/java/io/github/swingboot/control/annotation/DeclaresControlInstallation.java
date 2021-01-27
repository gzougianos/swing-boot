package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.annotation.installation.ControlInstallationFactory;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface DeclaresControlInstallation {
	Class<?>[] targetTypes();

	Class<? extends ControlInstallationFactory> factory();
}

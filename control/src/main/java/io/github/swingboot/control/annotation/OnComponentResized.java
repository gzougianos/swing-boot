package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.annotation.installation.OnComponentResizedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnComponentResizedInstallationFactory.class, targetTypes = Component.class)
public @interface OnComponentResized {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";
}

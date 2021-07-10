package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnComponentHiddenInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnComponentHiddenInstallationFactory.class, targetTypes = Component.class)
public @interface OnComponentHidden {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";
}

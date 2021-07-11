package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnPropertyChangeInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnPropertyChangeInstallationFactory.class, targetTypes = {
		Component.class })
public @interface OnPropertyChange {
	String ANY_PROPERTY = "ANY_PROPERTY__";

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	String property() default ANY_PROPERTY;

}

package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnWindowActivatedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnWindowActivatedInstallationFactory.class, targetTypes = {
		java.awt.Window.class })
public @interface OnWindowActivated {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	WindowState oldState() default WindowState.ANY;

	WindowState newState() default WindowState.ANY;

}

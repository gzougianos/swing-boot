package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.ItemSelectable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnItemStateChangedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnItemStateChangedInstallationFactory.class, targetTypes = {
		ItemSelectable.class })
public @interface OnItemStateChanged {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	BooleanValue selected() default BooleanValue.ANY;
}

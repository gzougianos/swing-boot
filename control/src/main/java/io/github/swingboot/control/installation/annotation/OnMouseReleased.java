package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnMouseReleasedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnMouseReleasedInstallationFactory.class, targetTypes = {
		Component.class })
public @interface OnMouseReleased {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	int button() default OnMouseConstants.ANY_BUTTON;

	int clickCount() default OnMouseConstants.ANY_CLICK_COUNT;

	int modifiers() default OnMouseConstants.ANY_MODIFIER;
}
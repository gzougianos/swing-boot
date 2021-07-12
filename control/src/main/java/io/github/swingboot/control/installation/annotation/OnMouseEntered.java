package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnMouseEnteredInstallationFactory;
import io.github.swingboot.control.installation.factory.MouseEventPredicate;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnMouseEnteredInstallationFactory.class, targetTypes = {
		Component.class })
public @interface OnMouseEntered {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	int button() default MouseEventPredicate.ANY_BUTTON;

	int clickCount() default MouseEventPredicate.ANY_CLICK_COUNT;

	int modifiers() default MouseEventPredicate.ANY_MODIFIER;
}
package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnKeyReleasedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnKeyReleasedInstallationFactory.class, targetTypes = {
		Component.class })
public @interface OnKeyReleased {
	int ANY_KEY_CODE = -999999;
	int ANY_MODIFIER = -999999;

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	int keyCode() default ANY_KEY_CODE;

	int modifiers() default ANY_MODIFIER;

}

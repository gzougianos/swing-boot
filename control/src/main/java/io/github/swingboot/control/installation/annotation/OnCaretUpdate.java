package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.text.JTextComponent;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnCaretUpdateInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnCaretUpdateInstallationFactory.class, targetTypes = {
		JTextComponent.class })
public @interface OnCaretUpdate {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

}

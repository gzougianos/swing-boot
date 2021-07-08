package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnKeyTypedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnKeyTypedInstallationFactory.class, targetTypes = { Component.class })
public @interface OnKeyTyped {

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	int keyCode() default OnKeyConstants.ANY_KEY_CODE;

	int modifiers() default OnKeyConstants.ANY_MODIFIER;

}
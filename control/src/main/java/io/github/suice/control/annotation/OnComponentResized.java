package io.github.suice.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.suice.control.Control;
import io.github.suice.control.annotation.installer.OnComponentResizedInstaller;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControl(installer = OnComponentResizedInstaller.class, targetTypes = Component.class)
public @interface OnComponentResized {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";
}

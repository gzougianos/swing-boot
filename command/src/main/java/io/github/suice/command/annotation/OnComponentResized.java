package io.github.suice.command.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.suice.command.Command;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresCommand(Component.class)
public @interface OnComponentResized {
	Class<? extends Command<?>> value();

	String id() default "";
}

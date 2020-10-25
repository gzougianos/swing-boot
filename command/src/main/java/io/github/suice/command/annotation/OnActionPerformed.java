package io.github.suice.command.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.AbstractButton;

import io.github.suice.command.Command;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresCommand(AbstractButton.class)
public @interface OnActionPerformed {
	public static final int ANY_MODIFIER = -500;

	Class<? extends Command<?>> value();

	int modifiers() default ANY_MODIFIER;

	String id() default "";

}

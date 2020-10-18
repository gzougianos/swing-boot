package io.github.suice.command.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.suice.command.Command;

@Target({ ElementType.FIELD })
@Retention(RUNTIME)
@Documented
public @interface OnActionPerformed {
	public static final int ANY_MODIFIER = -500;

	Class<? extends Command<?>> value();

	int modifiers() default ANY_MODIFIER;

}

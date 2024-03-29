package io.github.swingboot.control;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE })
@Inherited
public @interface InitializedBy {
	Class<? extends Control<?>> value();

	String parameterSource() default "";
}

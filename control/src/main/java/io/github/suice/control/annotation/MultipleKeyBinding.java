package io.github.suice.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@DeclaresMultipleControls(KeyBinding.class)
@Documented
public @interface MultipleKeyBinding {
	KeyBinding[] value();
}

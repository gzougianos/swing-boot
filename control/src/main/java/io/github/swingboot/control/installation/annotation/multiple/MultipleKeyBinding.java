package io.github.swingboot.control.installation.annotation.multiple;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.installation.annotation.KeyBinding;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@DeclaresMultipleControlInstallations
@Documented
public @interface MultipleKeyBinding {
	KeyBinding[] value();
}

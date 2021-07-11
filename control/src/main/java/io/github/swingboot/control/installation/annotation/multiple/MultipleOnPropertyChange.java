package io.github.swingboot.control.installation.annotation.multiple;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.installation.annotation.OnPropertyChange;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@DeclaresMultipleControlInstallations
@Documented
public @interface MultipleOnPropertyChange {
	OnPropertyChange[] value();
}

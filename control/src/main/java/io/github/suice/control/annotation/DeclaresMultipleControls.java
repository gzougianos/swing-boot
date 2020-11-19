package io.github.suice.control.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface DeclaresMultipleControls {
	Class<? extends Annotation> value();
}

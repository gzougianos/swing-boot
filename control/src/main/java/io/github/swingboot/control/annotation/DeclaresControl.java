package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.annotation.installer.AnnotationInstaller;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface DeclaresControl {
	Class<?>[] targetTypes();

	Class<? extends AnnotationInstaller> installer();
}

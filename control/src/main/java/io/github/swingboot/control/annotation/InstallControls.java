package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE, FIELD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface InstallControls {

	String[] ignoreIdsFromParent() default {};

	boolean ignoreAllIdsFromParent() default false;
}

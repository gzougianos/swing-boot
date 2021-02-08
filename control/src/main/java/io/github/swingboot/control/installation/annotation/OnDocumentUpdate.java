package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.text.JTextComponent;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnDocumentUpdateInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnDocumentUpdateInstallationFactory.class, targetTypes = {
		JTextComponent.class })
public @interface OnDocumentUpdate {

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

}

package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnActionPerformedInstallationFactory;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControlInstallation(factory = OnActionPerformedInstallationFactory.class, targetTypes = { AbstractButton.class,
		JTextField.class, JComboBox.class })
public @interface OnActionPerformed {
	public static final int ANY_MODIFIER = -500;

	Class<? extends Control<?>> value();

	int modifiers() default ANY_MODIFIER;

	String id() default "";

	String parameterSource() default "";

}

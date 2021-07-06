package io.github.swingboot.control.installation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Component;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.factory.OnKeyPressedInstallationFactory;
import io.github.swingboot.control.installation.factory.OnKeyReleasedInstallationFactory;
import io.github.swingboot.control.installation.factory.OnKeyTypedInstallationFactory;

public interface OnKey {
	int ANY_KEY_CODE = -999999;
	int ANY_MODIFIER = -999999;

	@Target({ FIELD, TYPE })
	@Retention(RUNTIME)
	@Documented
	@DeclaresControlInstallation(factory = OnKeyTypedInstallationFactory.class, targetTypes = {
			Component.class })
	public static @interface OnKeyTyped {

		Class<? extends Control<?>> value();

		String id() default "";

		String parameterSource() default "";

		int keyCode() default OnKey.ANY_KEY_CODE;

		int modifiers() default OnKey.ANY_MODIFIER;

	}

	@Target({ FIELD, TYPE })
	@Retention(RUNTIME)
	@Documented
	@DeclaresControlInstallation(factory = OnKeyReleasedInstallationFactory.class, targetTypes = {
			Component.class })
	public @interface OnKeyReleased {

		Class<? extends Control<?>> value();

		String id() default "";

		String parameterSource() default "";

		int keyCode() default OnKey.ANY_KEY_CODE;

		int modifiers() default OnKey.ANY_MODIFIER;

	}

	@Target({ FIELD, TYPE })
	@Retention(RUNTIME)
	@Documented
	@DeclaresControlInstallation(factory = OnKeyPressedInstallationFactory.class, targetTypes = {
			Component.class })
	public @interface OnKeyPressed {

		Class<? extends Control<?>> value();

		String id() default "";

		String parameterSource() default "";

		int keyCode() default OnKey.ANY_KEY_CODE;

		int modifiers() default OnKey.ANY_MODIFIER;

	}
}

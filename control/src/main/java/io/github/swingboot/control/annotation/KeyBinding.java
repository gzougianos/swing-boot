package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.KeyStroke;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.annotation.installer.KeyBindingInstaller;
import io.github.swingboot.control.annotation.multiple.MultipleKeyBinding;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@Repeatable(MultipleKeyBinding.class)
//@formatter:off
@DeclaresControl(installer = KeyBindingInstaller.class, 
		targetTypes = { JComponent.class, JFrame.class, JWindow.class, JDialog.class })
//@formatter:on
public @interface KeyBinding {
	/**
	 * @see JComponent#WHEN_FOCUSED
	 */
	public static final int WHEN_FOCUSED = JComponent.WHEN_FOCUSED;

	/**
	 * @see JComponent#WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
	 */
	public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

	/**
	 * @see JComponent#WHEN_IN_FOCUSED_WINDOW
	 */
	public static final int WHEN_IN_FOCUSED_WINDOW = JComponent.WHEN_IN_FOCUSED_WINDOW;

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	/** One of {@link #WHEN_ANCESTOR_OF_FOCUSED_COMPONENT}, {@link #WHEN_FOCUSED} and {@link #WHEN_IN_FOCUSED_WINDOW}
	 * @see JComponent#getInputMap(int)
	 * @return The condition of the input map.
	 */
	int when() default WHEN_FOCUSED;

	/**
	 * @see KeyStroke#getKeyStroke(String)
	 * @return The keystroke described as a String.
	 */
	String keyStroke();

}

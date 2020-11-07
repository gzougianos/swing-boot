package io.github.suice.control.annotation.listener;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.KeyStroke;

import io.github.suice.control.Control;
import io.github.suice.control.annotation.DeclaresControl;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControl(value = { JComponent.class, JFrame.class, JWindow.class, JDialog.class })
public @interface KeyBinding {
	/**
	 * @see {@link JComponent#WHEN_FOCUSED}
	 */
	public static final int WHEN_FOCUSED = JComponent.WHEN_FOCUSED;

	/**
	 * @see {@link JComponent#WHEN_ANCESTOR_OF_FOCUSED_COMPONENT}
	 */
	public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

	/**
	 * @see {@link JComponent#WHEN_IN_FOCUSED_WINDOW}
	 */
	public static final int WHEN_IN_FOCUSED_WINDOW = JComponent.WHEN_IN_FOCUSED_WINDOW;

	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	/** One of {@link #WHEN_ANCESTOR_OF_FOCUSED_COMPONENT}, {@link #WHEN_FOCUSED} and {@link #WHEN_IN_FOCUSED_WINDOW}
	 * @see {@link JComponent#getInputMap(int)}
	 * @return The condition of the input map.
	 */
	int when() default WHEN_FOCUSED;

	/**
	 * @see {@link KeyStroke#getKeyStroke(String)}
	 * @return The keystroke described as a String.
	 */
	String keyStroke();

}

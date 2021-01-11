package io.github.swingboot.control.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.annotation.installer.OnSelectionChangedInstaller;

@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Documented
@DeclaresControl(installer = OnSelectionChangedInstaller.class, targetTypes = { JTable.class, JList.class,
		ListSelectionModel.class })
public @interface OnSelectionChanged {
	Class<? extends Control<?>> value();

	String id() default "";

	String parameterSource() default "";

	BooleanValue valueIsAdjusting() default BooleanValue.ANY;
}

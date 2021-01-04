package io.github.swingboot.concurrency;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;

public class LogFieldValueChanger {
	private Field logField;

	public LogFieldValueChanger(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType() == Logger.class) {
				if (!Modifier.isStatic(field.getModifiers())) {
					throw new RuntimeException("Log field is not static.");
				}
				this.logField = field;
				return;
			}
		}
	}

	static void setValueToStaticFinalField(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	public void replaceWith(Logger logger) throws Exception {
		setValueToStaticFinalField(logField, logger);
	}
}

package io.github.suice.command.reflect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class FieldOrMethodTests {
	@SuppressWarnings("unused")
	private int x;

	@Test
	void testField() throws Exception {
		Field field = FieldOrMethodTests.class.getDeclaredField("x");
		FieldOrMethod fom = new FieldOrMethod(field);

		assertEquals(field, fom.getAccessibleObject());

		assertTrue(fom.isField());
		assertFalse(fom.isMethod());
		assertEquals(int.class, fom.getValueReturnType());
		assertEquals(FieldOrMethodTests.class, fom.getDeclaringClass());

		assertFalse(field.isAccessible());

		fom.ensureAccess();

		assertTrue(field.isAccessible());

		assertEquals(fom, fom);
		assertEquals(fom.hashCode(), fom.hashCode());

		FieldOrMethod fom2 = new FieldOrMethod(FieldOrMethodTests.class.getDeclaredField("x"));
		assertEquals(fom, fom2);
	}

	@Test
	void testMethod() throws Exception {
		Method method = FieldOrMethodTests.class.getDeclaredMethod("testField");
		FieldOrMethod fom = new FieldOrMethod(method);

		assertEquals(method, fom.getAccessibleObject());

		assertFalse(fom.isField());
		assertTrue(fom.isMethod());

		assertEquals(void.class, fom.getValueReturnType());
		assertEquals(FieldOrMethodTests.class, fom.getDeclaringClass());

		assertFalse(method.isAccessible());

		fom.ensureAccess();

		assertTrue(method.isAccessible());

		assertEquals(fom, fom);
		assertEquals(fom.hashCode(), fom.hashCode());

		FieldOrMethod fom2 = new FieldOrMethod(FieldOrMethodTests.class.getDeclaredMethod("testField"));
		assertEquals(fom, fom2);
	}

	@Test
	void exceptionWhenNotFieldOrMethod() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> new FieldOrMethod(FieldOrMethod.class.getDeclaredConstructors()[0]));
	}
}

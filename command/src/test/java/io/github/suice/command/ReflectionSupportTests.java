package io.github.suice.command;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

class ReflectionSupportTests {

	@Test
	void getDeclaredAndInheritedFieldsFromAnnotatedType() throws Exception {

		Set<Field> fields = ReflectionSupport.getDeclaredAndInheritedFields(Child.class, InstallCommands.class);
		assertEquals(3, fields.size());
		assertTrue(fields.contains(Child.class.getDeclaredField("childField")));
		assertTrue(fields.contains(Child.class.getDeclaredField("protectedChildField")));
		assertTrue(fields.contains(Child.class.getDeclaredField("publicChildField")));

		fields = ReflectionSupport.getDeclaredAndInheritedFields(GrandChild.class, InstallCommands.class);
		assertEquals(4, fields.size());
		assertTrue(fields.contains(Child.class.getDeclaredField("childField")));
		assertTrue(fields.contains(GrandChild.class.getDeclaredField("grandField")));
		assertTrue(fields.contains(Child.class.getDeclaredField("protectedChildField")));
		assertTrue(fields.contains(Child.class.getDeclaredField("publicChildField")));

		fields = ReflectionSupport.getDeclaredAndInheritedFields(Parent.class, InstallCommands.class);
		assertEquals(2, fields.size());
		assertTrue(fields.contains(Parent.class.getDeclaredField("field")));
		assertTrue(fields.contains(Parent.class.getDeclaredField("field2")));
	}

	@Test
	void hasMethod() throws Exception {
		assertTrue(ReflectionSupport.hasMethod("addActionListener", JButton.class));
		assertTrue(ReflectionSupport.hasMethod("addActionListener", AbstractButton.class));
		assertFalse(ReflectionSupport.hasMethod("addActionListener", JPanel.class));
		assertTrue(ReflectionSupport.hasMethod("length", String.class));
	}

	@SuppressWarnings("unused")
	private class Parent {
		private String field;
		protected String field2;
	}

	@InstallCommands
	@SuppressWarnings("unused")
	private class Child extends Parent {
		private String childField;
		protected String protectedChildField;
		public String publicChildField;
		private static final String someStaticField = "s";
	}

	@InstallCommands
	@SuppressWarnings("unused")
	private class GrandChild extends Child {
		private String grandField;
	}

}

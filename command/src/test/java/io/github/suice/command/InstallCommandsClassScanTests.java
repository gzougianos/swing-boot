package io.github.suice.command;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Set;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;

class InstallCommandsClassScanTests {

	@Test
	void sigleClass() throws Exception {
		Set<Field> fields = new InstallCommandsClassScan(Parent.class).getAnnotatedComponentFields();
		assertEquals(1, fields.size());
		assertTrue(fields.contains(Parent.class.getDeclaredField("componentField")));
	}

	@Test
	void fromParentWhenParentIsNotAnnotatedWithInstallCommands() throws Exception {
		Set<Field> fields = new InstallCommandsClassScan(Child.class).getAnnotatedComponentFields();
		assertEquals(1, fields.size());
		assertTrue(fields.contains(Child.class.getDeclaredField("componentField")));
	}

	@Test
	void fromParentWhenParentIsAnnotatedWithInstallCommands() throws Exception {
		Set<Field> fields = new InstallCommandsClassScan(GrandChild.class).getAnnotatedComponentFields();
		assertEquals(3, fields.size());
		assertTrue(fields.contains(Child.class.getDeclaredField("componentField")));
		assertTrue(fields.contains(GrandChild.class.getDeclaredField("componentField")));
		assertTrue(fields.contains(GrandChild.class.getDeclaredField("componentField1")));
	}

	@SuppressWarnings("unused")
	private class Parent {
		@OnActionPerformed(TestCommand.class)
		private String stringField;

		@OnActionPerformed(TestCommand.class)
		private Component componentField;
		protected String field2;
	}

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}

	@InstallCommands
	@SuppressWarnings("unused")
	private class Child extends Parent {
		@OnActionPerformed(TestCommand.class)
		private String childField;

		@OnActionPerformed(TestCommand.class)
		protected String protectedChildField;

		@OnActionPerformed(TestCommand.class)
		protected Component componentField;
		public String publicChildField;
		private static final String someStaticField = "s";
	}

	@InstallCommands
	@SuppressWarnings("unused")
	private class GrandChild extends Child {
		private String grandField;

		@OnActionPerformed(TestCommand.class)
		private Component componentField;

		@OnActionPerformed(TestCommand.class)
		private JButton componentField1;
	}

}

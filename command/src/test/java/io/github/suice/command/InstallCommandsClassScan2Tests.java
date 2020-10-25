package io.github.suice.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.OnComponentResized;
import io.github.suice.command.annotation.ParameterSource;

class InstallCommandsClassScan2Tests {

	@Test
	void noInheritance() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(Parent.class);

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				Parent.class.getDeclaredField("button"),
				Parent.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(Parent.class.getDeclaredField("field"));

		AnnotatedComponentField expectedFieldComponentAnnotation2 = new AnnotatedComponentField(
				Parent.class.getDeclaredField("button2"),
				Parent.class.getDeclaredField("button2").getAnnotation(OnActionPerformed.class));

		assertEquals(2, scan.getAnnotatedComponentFields().size());
		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));
		assertEquals(expectedFieldComponentAnnotation2, scan.getAnnotatedComponentFields().get("id2"));
	}

	@Test
	void inheritanceIgnoreOne() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(ChildIgnoreOne.class);
		assertEquals(2, scan.getAnnotatedComponentFields().size());

		//Inherited parameter source
		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				Parent.class.getDeclaredField("button"),
				Parent.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(ChildIgnoreOne.class.getDeclaredField("fielde"));

		AnnotatedComponentField expectedFieldComponentAnnotation2 = new AnnotatedComponentField(
				ChildIgnoreOne.class.getDeclaredField("button"),
				ChildIgnoreOne.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation2.setParamaterSource(ChildIgnoreOne.class.getDeclaredField("button"));

		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));
		assertEquals(expectedFieldComponentAnnotation2, scan.getAnnotatedComponentFields().get("id2"));
	}

	@Test
	void inheritanceIgnoreNone() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(ChildIgnoreNone.class);
		assertEquals(2, scan.getAnnotatedComponentFields().size());

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				Parent.class.getDeclaredField("button"),
				Parent.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(ChildIgnoreNone.class.getDeclaredField("fielde"));

		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));

		AnnotatedComponentField expectedFieldComponentAnnotation2 = new AnnotatedComponentField(
				ChildIgnoreOne.class.getDeclaredField("button"),
				ChildIgnoreOne.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation2.setParamaterSource(ChildIgnoreOne.class.getDeclaredField("button"));
		assertEquals(expectedFieldComponentAnnotation2, scan.getAnnotatedComponentFields().get("id2"));
	}

	@Test
	void childDeclaresSameIdThatExistsInParent() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(ChildDeclaresSameIdAsParent.class);
		assertEquals(2, scan.getAnnotatedComponentFields().size());

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				ChildDeclaresSameIdAsParent.class.getDeclaredField("button1"),
				ChildDeclaresSameIdAsParent.class.getDeclaredField("button1").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(Parent.class.getDeclaredField("field"));

		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));

		AnnotatedComponentField expectedFieldComponentAnnotation2 = new AnnotatedComponentField(
				Parent.class.getDeclaredField("button2"),
				Parent.class.getDeclaredField("button2").getAnnotation(OnActionPerformed.class));

		assertEquals(expectedFieldComponentAnnotation2, scan.getAnnotatedComponentFields().get("id2"));
	}

	@Test
	void inheritanceIgnoreAll() {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(ChildIgnoreAll.class);
		assertEquals(0, scan.getAnnotatedComponentFields().size());
	}

	@Test
	void methodSource() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(MethodSource.class);
		assertEquals(1, scan.getAnnotatedComponentFields().size());

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				MethodSource.class.getDeclaredField("button"),
				MethodSource.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(MethodSource.class.getDeclaredMethod("source", AWTEvent.class));

		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));
	}

	@Test
	void staticFieldSource() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(StaticFieldSource.class);
		assertEquals(1, scan.getAnnotatedComponentFields().size());

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				StaticFieldSource.class.getDeclaredField("button"),
				StaticFieldSource.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		expectedFieldComponentAnnotation1.setParamaterSource(StaticFieldSource.class.getDeclaredField("source"));

		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id"));
	}

	@Test
	void duplicateParameterSource() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(DuplicateParameterSource.class));
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(DuplicateParameterSourceMethod.class));
	}

	@Test
	void wrongMethodSourceParameters() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(WrongSourceMethodParameters.class));
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(WrongSourceMethodParameters2.class));
	}

	@Test
	void annotationComponentTypeMismatch() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(AnnotationComponentTypeMismatch.class));
	}

	@Test
	void duplicateId() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(DuplicateAnnotationId.class));
	}

	@Test
	void voidParameterSourceMethod() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(VoidParameterSourceMethod.class));
	}

	@Test
	void twoAnnotationsInTheSameFieldWithSameId() {
		assertThrows(Exception.class, () -> new InstallCommandsClassAnalysis(TwoAnnotationsInTheSameFieldWithSameId.class));
	}

	@Test
	void twoAnnotationsInTheSameField() throws Exception {
		InstallCommandsClassAnalysis scan = new InstallCommandsClassAnalysis(TwoAnnotationsInTheSameField.class);
		assertEquals(2, scan.getAnnotatedComponentFields().size());

		AnnotatedComponentField expectedFieldComponentAnnotation1 = new AnnotatedComponentField(
				TwoAnnotationsInTheSameField.class.getDeclaredField("button"),
				TwoAnnotationsInTheSameField.class.getDeclaredField("button").getAnnotation(OnActionPerformed.class));
		assertEquals(expectedFieldComponentAnnotation1, scan.getAnnotatedComponentFields().get("id2"));

		AnnotatedComponentField expectedFieldComponentAnnotation2 = new AnnotatedComponentField(
				TwoAnnotationsInTheSameField.class.getDeclaredField("button"),
				TwoAnnotationsInTheSameField.class.getDeclaredField("button").getAnnotation(OnComponentResized.class));
		assertEquals(expectedFieldComponentAnnotation2, scan.getAnnotatedComponentFields().get("id"));
	}

	@InstallCommands
	private static class Parent {
		@ParameterSource("id")
		private int field;

		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton button;

		@OnActionPerformed(value = TestCommand.class, id = "id2")
		private JButton button2;
		private JPanel panel;
		private static int staticField;
		private int fieldWithoutAnything;
	}

	@InstallCommands(ignoreIdsFromParent = "id2")
	private static class ChildIgnoreOne extends Parent {
		@ParameterSource("id")
		private int fielde;

		@ParameterSource("id2")
		@OnActionPerformed(value = TestCommand.class, id = "id2")
		private JButton button;
	}

	@InstallCommands
	private static class ChildIgnoreNone extends ChildIgnoreOne {
		@ParameterSource("id")
		private String fielde;
	}

	@InstallCommands(ignoreAllIdsFromParent = true)
	private static class ChildIgnoreAll extends ChildIgnoreNone {

	}

	@InstallCommands
	private static class ChildDeclaresSameIdAsParent extends Parent {
		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton button1;
	}

	@InstallCommands
	private static class TwoAnnotationsInTheSameField {
		@OnComponentResized(value = TestCommand.class, id = "id")
		@OnActionPerformed(value = TestCommand.class, id = "id2")
		private JButton button;
	}

	@InstallCommands
	private static class TwoAnnotationsInTheSameFieldWithSameId {
		@OnComponentResized(value = TestCommand.class, id = "id2")
		@OnActionPerformed(value = TestCommand.class, id = "id2")
		private JButton button2;
	}

	@InstallCommands
	private static class MethodSource {
		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton button;

		@ParameterSource("id")
		protected int source(AWTEvent event) {
			return -1;
		}

		private void notMethodSource() {

		}
	}

	@InstallCommands
	private static class StaticFieldSource {
		@ParameterSource("id")
		private static final String source = "";
		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton button;
	}

	private static class DuplicateParameterSource {
		@ParameterSource("id")
		private int field;

		@ParameterSource("id")
		private int field2;

		@OnActionPerformed(TestCommand.class)
		private JButton button;
	}

	private static class DuplicateParameterSourceMethod {
		@ParameterSource("id")
		private int field;

		@ParameterSource("id")
		private int method() {
			return 1;
		}

		@OnActionPerformed(TestCommand.class)
		private JButton button;
	}

	private static class WrongSourceMethodParameters {
		@ParameterSource("id")
		private int method(int i) {
			return 1;
		}
	}

	private static class WrongSourceMethodParameters2 {
		@ParameterSource("id")
		private int method(AWTEvent ev, int k) {
			return 1;
		}
	}

	private static class VoidParameterSourceMethod {
		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton b1;

		@ParameterSource("id")
		private void method() {

		}
	}

	private static class AnnotationComponentTypeMismatch {
		@OnActionPerformed(TestCommand.class)
		private JPanel panel;
	}

	private static class DuplicateAnnotationId {
		@OnActionPerformed(value = TestCommand.class, id = "id")
		private JButton b1;

		@OnComponentResized(value = TestCommand.class, id = "id")
		private AbstractButton b2;
	}

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {

		}
	}
}

package io.github.suice.control;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.OnComponentResized;

class ObjectOwnedControlDeclarationTests {

	@Test
	void targetsType() {
		AnnotationOnType owner = new AnnotationOnType();
		ObjectOwnedControlDeclaration declaration = new ObjectOwnedControlDeclaration(owner,
				new ControlDeclaration(AnnotationOnType.class.getAnnotation(OnComponentResized.class), AnnotationOnType.class));
		assertEquals(owner, declaration.getTargetObject());
	}

	@Test
	void targetsAField() throws Exception {
		AnnotationOnField owner = new AnnotationOnField();
		Field field = AnnotationOnField.class.getDeclaredField("panel");
		ObjectOwnedControlDeclaration declaration = new ObjectOwnedControlDeclaration(owner,
				new ControlDeclaration(field.getAnnotation(OnComponentResized.class), field));
		assertEquals(owner.panel, declaration.getTargetObject());
		assertTrue(field.isAccessible());
	}

	@SuppressWarnings("serial")
	@OnComponentResized(value = TestControl.class)
	private static class AnnotationOnType extends JPanel {

	}

	private static class AnnotationOnField {
		@OnComponentResized(value = TestControl.class)
		private JPanel panel = new JPanel();
	}

	private static class TestControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}
}

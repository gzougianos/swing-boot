package io.github.swingboot.control.declaration;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.control.installation.factory.OnActionPerformedInstallationFactory;
import io.github.swingboot.control.parameter.ParameterSource;

@SuppressWarnings("unused")
class ControlInstallationDeclarationTests {

	@Nested
	class NotADeclaresControlAnnotation {

		@Inject
		private int x;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("x");
			assertThrows(InvalidControlDeclarationException.class,
					() -> new ControlInstallationDeclaration(field.getAnnotation(Inject.class), field));
		}
	}

	@OnActionPerformed(VoidControl.class)
	@Nested
	class AnnotationDoesNotSupportTargetElement {

		@OnActionPerformed(VoidControl.class)
		private JPanel panel;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("panel");
			assertThrows(InvalidControlDeclarationException.class,
					() -> new ControlInstallationDeclaration(field.getAnnotation(OnActionPerformed.class),
							field));

			assertThrows(InvalidControlDeclarationException.class, () -> new ControlInstallationDeclaration(
					this.getClass().getAnnotation(OnActionPerformed.class), getClass()));
		}
	}

	@Nested
	class InstallationTarget {
		@Test
		void fieldTarget() throws Exception {
			Field field = HasAnnotationOnField.class.getDeclaredField("button");
			ControlInstallationDeclaration declaration = new ControlInstallationDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			HasAnnotationOnField fieldOwner = new HasAnnotationOnField();
			assertSame(fieldOwner.button, declaration.getInstallationTargetFor(fieldOwner));

			fieldOwner.button = null;
			assertThrows(NullPointerException.class, () -> declaration.getInstallationTargetFor(fieldOwner));
		}

		@Test
		void classTarget() throws Exception {
			ControlInstallationDeclaration declaration = new ControlInstallationDeclaration(
					HasAnnotationOnClass.class.getAnnotation(OnActionPerformed.class),
					HasAnnotationOnClass.class);

			HasAnnotationOnClass hasAnnotationOnClass = new HasAnnotationOnClass();
			assertSame(hasAnnotationOnClass, declaration.getInstallationTargetFor(hasAnnotationOnClass));
		}

		class HasAnnotationOnField {
			@OnActionPerformed(value = IntControl.class, id = "the_id", parameterSource = "parsource")
			JButton button = new JButton();
		}

		@OnActionPerformed(value = IntControl.class, id = "the_id", parameterSource = "parsource")
		@SuppressWarnings("serial")
		class HasAnnotationOnClass extends JButton {
		}
	}

	@Nested
	class AllGood {
		@OnActionPerformed(value = IntControl.class, id = "the_id", parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlInstallationDeclaration declaration = new ControlInstallationDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			ParameterSource source = mock(ParameterSource.class);
			doReturn(Integer.class).when(source).getValueReturnType();
			doReturn("parsource").when(source).getId();

			declaration.setParameterSource(source);
			assertEquals("the_id", declaration.getId());
			assertEquals("parsource", declaration.getParameterSourceId());
			assertEquals(field, declaration.getTargetElement());
			assertTrue(declaration.getParameterSource().isPresent());
			assertEquals(OnActionPerformedInstallationFactory.class, declaration.getInstallerType());
		}
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}

	private static class IntControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}

}

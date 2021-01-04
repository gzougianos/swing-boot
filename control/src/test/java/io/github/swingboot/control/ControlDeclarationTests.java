package io.github.swingboot.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.swingboot.control.annotation.OnActionPerformed;
import io.github.swingboot.control.annotation.ParameterSource;
import io.github.swingboot.control.annotation.installer.OnActionPerformedInstaller;

@SuppressWarnings("unused")
class ControlDeclarationTests {

	@Nested
	class NotADeclaresControlAnnotation {

		@Inject
		private int x;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("x");
			assertThrows(InvalidControlDeclarationException.class,
					() -> new ControlDeclaration(field.getAnnotation(Inject.class), field));
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
					() -> new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field));

			assertThrows(InvalidControlDeclarationException.class,
					() -> new ControlDeclaration(this.getClass().getAnnotation(OnActionPerformed.class),
							getClass()));
		}
	}

	@Nested
	class ExceptionWhenParameterSourceGivenOnParameterlessControl {
		@OnActionPerformed(value = VoidControl.class, parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlDeclaration controlDeclaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);
			assertFalse(controlDeclaration.expectsParameterSource());
		}
	}

	@Nested
	class ExceptionWhenSettingParameterSourceOnParameterlessControl {
		@OnActionPerformed(value = VoidControl.class)
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			io.github.swingboot.control.parameter.ParameterSource source = mock(
					io.github.swingboot.control.parameter.ParameterSource.class);

			ControlDeclaration declaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);
			assertThrows(InvalidControlDeclarationException.class,
					() -> declaration.setParameterSource(source));
		}
	}

	@Nested
	class ExceptionWhenParameterSourceReturnValueMismatchControlParameterType {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlDeclaration declaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			io.github.swingboot.control.parameter.ParameterSource source = mock(
					io.github.swingboot.control.parameter.ParameterSource.class);
			doReturn(String.class).when(source).getValueReturnType();
			doReturn("parsource").when(source).getId();
			assertThrows(InvalidControlDeclarationException.class,
					() -> declaration.setParameterSource(source));
		}

		@ParameterSource("parsource")
		public String wrongReturnValue() {
			return null;
		}
	}

	@Nested
	class ExceptionWhenParameterSourceNotGivenOnNonNullableParameterControl {
		@OnActionPerformed(value = IntControl.class)
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			assertThrows(InvalidControlDeclarationException.class,
					() -> new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field));
		}
	}

	@Nested
	class NoExceptionWhenParameterSourceNotGivenOnNullableParameterControl {
		@OnActionPerformed(value = IntNullableControl.class)
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			assertDoesNotThrow(
					() -> new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field));
		}
	}

	@Nested
	class settingAParameterSourceThatReturnsVoid {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlDeclaration declaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			io.github.swingboot.control.parameter.ParameterSource source = mock(
					io.github.swingboot.control.parameter.ParameterSource.class);
			doReturn(Void.class).when(source).getValueReturnType();
			doReturn("parsource").when(source).getId();
			assertThrows(InvalidControlDeclarationException.class,
					() -> declaration.setParameterSource(source));
		}
	}

	@Nested
	class ParameterSourceIdMismatch {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlDeclaration declaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			io.github.swingboot.control.parameter.ParameterSource source = mock(
					io.github.swingboot.control.parameter.ParameterSource.class);
			doReturn(Integer.class).when(source).getValueReturnType();
			doReturn("A MISMATCHED ID").when(source).getId();
			assertThrows(InvalidControlDeclarationException.class,
					() -> declaration.setParameterSource(source));
		}
	}

	@Nested
	class AllGood {
		@OnActionPerformed(value = IntControl.class, id = "the_id", parameterSource = "parsource")
		private JButton field;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("field");
			ControlDeclaration declaration = new ControlDeclaration(
					field.getAnnotation(OnActionPerformed.class), field);

			io.github.swingboot.control.parameter.ParameterSource source = mock(
					io.github.swingboot.control.parameter.ParameterSource.class);
			doReturn(Integer.class).when(source).getValueReturnType();
			doReturn("parsource").when(source).getId();

			declaration.setParameterSource(source);
			assertEquals("the_id", declaration.getId());
			assertEquals("parsource", declaration.getParameterSourceId());
			assertEquals(Integer.class, declaration.getControlTypeInfo().getParameterType());
			assertEquals(IntControl.class, declaration.getControlTypeInfo().getControlType());
			assertEquals(field, declaration.getTargetElement());
			assertTrue(declaration.getParameterSource().isPresent());
			assertEquals(OnActionPerformedInstaller.class, declaration.getInstallerType());
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

	private static class IntNullableControl implements Control<Integer> {
		@Override
		public void perform(@Nullable Integer parameter) {
		}
	}
}

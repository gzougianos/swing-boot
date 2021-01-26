package io.github.swingboot.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import io.github.swingboot.control.parameter.ParameterSource;

class ParameterSourceContextTests {

	@Test
	void expectParameterSource() {
		ParameterSourceContext context = create(VoidControl.class, "id");
		assertFalse(context.expectsParameterSource());

		context = create(VoidControl.class, "");
		assertFalse(context.expectsParameterSource());

		context = create(IntControl.class, "id");
		assertTrue(context.expectsParameterSource());
	}

	@Test
	void exceptionBasedOnControlParameterNullability() {
		assertThrows(InvalidParameterSourceDeclarationException.class,
				create(IntControl.class, "")::checkIfParameterSourceGivenWhenNonNullableParameter);

		assertDoesNotThrow(
				create(IntNullableControl.class, "")::checkIfParameterSourceGivenWhenNonNullableParameter);
		assertDoesNotThrow(
				create(VoidControl.class, "")::checkIfParameterSourceGivenWhenNonNullableParameter);
	}

	@Test
	void exceptionSettingParameterSourceToVoidControl() {
		ParameterSourceContext context = create(VoidControl.class, "");

		assertThrows(InvalidParameterSourceDeclarationException.class,
				() -> context.setParameterSource(mock(ParameterSource.class)));
	}

	@Test
	void settingParameterSourceWithDifferentId() {
		ParameterSourceContext context = create(IntControl.class, "id1");

		ParameterSource source = mock(ParameterSource.class);
		when(source.getId()).thenReturn("differentId");
		assertThrows(InvalidParameterSourceDeclarationException.class,
				() -> context.setParameterSource(source));
	}

	@Test
	void exceptionWhenParameterSourceReturnTypeMismatchesControlParameterType() {
		ParameterSourceContext context = create(IntControl.class, "id");

		ParameterSource source = mock(ParameterSource.class);
		when(source.getId()).thenReturn("id");
		doReturn(String.class).when(source).getValueReturnType();
		assertThrows(InvalidParameterSourceDeclarationException.class,
				() -> context.setParameterSource(source));
	}

	@Test
	void parameterSourceOk() {
		ParameterSourceContext context = create(IntControl.class, "id");

		ParameterSource source = mock(ParameterSource.class);
		when(source.getId()).thenReturn("id");
		doReturn(int.class).when(source).getValueReturnType(); //checks indirectly equals/extends also
		context.setParameterSource(source);
		assertSame(source, context.getParameterSource().get());
	}

	private ParameterSourceContext create(Class<? extends Control<?>> control, String id) {
		return new ParameterSourceContext(ControlTypeInfo.of(control), id, this);
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

package io.github.swingboot.control.declaration;

import static io.github.swingboot.control.declaration.ControlTypeInfo.of;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import io.github.swingboot.control.Control;

class ControlTypeInfoTests {

	@SuppressWarnings("unchecked")
	@Test
	void parameterType() {
		assertEquals(Void.class, of(VoidControl.class).getParameterType());
		assertEquals(String.class, of(StringControl.class).getParameterType());

		Control<Integer> integerC = new Control<Integer>() {

			@Override
			public void perform(Integer parameter) {
			}
		};
		assertEquals(Integer.class, of((Class<? extends Control<?>>) integerC.getClass()).getParameterType());

		assertEquals(Double.class, of(GenericChildControl.class).getParameterType());
		assertEquals(Optional.class, of(NestedGeneric.class).getParameterType());
		assertEquals(Integer.class, of(TwoGenericInterfaces.class).getParameterType());
		assertEquals(Double.class, of(AbstractControl.class).getParameterType());
		assertEquals(String.class, of(SameMethodName.class).getParameterType());
	}

	@Test
	void caching() {
		ControlTypeInfo info = of(VoidControl.class);
		assertSame(info, of(VoidControl.class));
	}

	@Test
	void parameterLess() {
		assertTrue(of(VoidControl.class).isParameterless());
		assertFalse(of(StringControl.class).isParameterless());
	}

	@Test
	void nullable() {
		assertTrue(of(NullableParameter.class).isParameterNullable());
		assertFalse(of(AbstractControl.class).isParameterNullable());
		assertTrue(of(AbstractControlNullableMethod.class).isParameterNullable());
		assertFalse(of(ChildNotNullableParameter.class).isParameterNullable());
		assertTrue(of(SameMethodName.class).isParameterNullable());
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}

	private static class StringControl implements Control<String> {
		@Override
		public void perform(String parameter) {
		}
	}

	private static class GenericControl<X> implements Control<X> {
		@Override
		public void perform(X parameter) {
		}
	}

	private static class GenericChildControl extends GenericControl<Double> {
		@Override
		public void perform(Double parameter) {
			super.perform(parameter);
		}

		@SuppressWarnings("unused")
		public void perform(Integer obj) {
		}

	}

	private static abstract class AbstractControl implements Control<Double> {

	}

	private static class NestedGeneric implements Control<Optional<Integer>> {
		@Override
		public void perform(Optional<Integer> parameter) {
		}
	}

	private static class TwoGenericInterfaces implements Control<Integer>, GenericInterface<String> {
		@Override
		public void perform(Integer parameter) {
		}

		@Override
		public void aMethod(String par) {
		}
	}

	private static interface GenericInterface<T> {
		void aMethod(T par);
	}

	private static class NullableParameter implements Control<CharSequence> {
		@Override
		public void perform(@Nullable CharSequence parameter) {
		}
	}

	private static abstract class AbstractControlNullableMethod implements Control<Double> {
		@Override
		public void perform(@Nullable Double parameter) {
		}
	}

	private static class ChildNotNullableParameter extends AbstractControlNullableMethod {
		@Override
		public void perform(Double parameter) {
		}
	}

	private static class SameMethodName implements Control<String> {
		@Override
		public void perform(@Nullable String parameter) {
		}

		@SuppressWarnings("unused")
		public void perform(Double parameter) {
		}
	}
}

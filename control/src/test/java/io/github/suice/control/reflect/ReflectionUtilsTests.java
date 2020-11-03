package io.github.suice.control.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.Optional;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.control.Control;

@SuppressWarnings("unused")
class ReflectionUtilsTests {

	@Test
	void equalsExtends() {
		assertTrue(ReflectionUtils.equalsOrExtends(Component.class, Component.class));
		assertTrue(ReflectionUtils.equalsOrExtends(JPanel.class, Component.class));
		assertTrue(ReflectionUtils.equalsOrExtends(String.class, CharSequence.class));
		assertFalse(ReflectionUtils.equalsOrExtends(String.class, Component.class));
		assertTrue(ReflectionUtils.equalsOrExtends(JButton.class, AbstractButton.class));
		assertTrue(ReflectionUtils.equalsOrExtends(Integer.class, int.class));
		assertTrue(ReflectionUtils.equalsOrExtends(int.class, Integer.class));
		assertFalse(ReflectionUtils.equalsOrExtends(float.class, Integer.class));
		assertFalse(ReflectionUtils.equalsOrExtends(Integer.class, float.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void getControlParameterType() {
		assertEquals(Void.class, ReflectionUtils.getControlParameterType(VoidControl.class));
		assertEquals(Void.class, ReflectionUtils.getControlParameterType(VoidControl.class)); //cache test
		assertEquals(String.class, ReflectionUtils.getControlParameterType(StringControl.class));

		Control<Integer> integerC = new Control<Integer>() {

			@Override
			public void perform(Integer parameter) {
			}
		};
		assertEquals(Integer.class, ReflectionUtils.getControlParameterType((Class<? extends Control<?>>) integerC.getClass()));

		assertEquals(Double.class, ReflectionUtils.getControlParameterType(GenericChildControl.class));
		assertEquals(Optional.class, ReflectionUtils.getControlParameterType(NestedGeneric.class));
		assertEquals(Integer.class, ReflectionUtils.getControlParameterType(TwoGenericInterfaces.class));
		assertEquals(Double.class, ReflectionUtils.getControlParameterType(AbstractControl.class));
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

}

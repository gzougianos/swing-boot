package io.github.suice.command.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.command.Command;

class ReflectionUtilsTests {

	@Test
	void hasMethod() throws Exception {
		assertTrue(ReflectionUtils.hasMethod("addActionListener", JButton.class));
		assertTrue(ReflectionUtils.hasMethod("addActionListener", AbstractButton.class));
		assertFalse(ReflectionUtils.hasMethod("addActionListener", JPanel.class));
		assertTrue(ReflectionUtils.hasMethod("length", String.class));
	}

	@Test
	void equalsExtends() {
		assertTrue(ReflectionUtils.equalsOrExtends(Component.class, Component.class));
		assertTrue(ReflectionUtils.equalsOrExtends(JPanel.class, Component.class));
		assertFalse(ReflectionUtils.equalsOrExtends(String.class, Component.class));
		assertTrue(ReflectionUtils.equalsOrExtends(JButton.class, AbstractButton.class));
		assertTrue(ReflectionUtils.equalsOrExtends(Integer.class, int.class));
		assertTrue(ReflectionUtils.equalsOrExtends(int.class, Integer.class));
		assertFalse(ReflectionUtils.equalsOrExtends(float.class, Integer.class));
		assertFalse(ReflectionUtils.equalsOrExtends(Integer.class, float.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void getCommandGenericParameterType() {
		assertEquals(Void.class, ReflectionUtils.getCommandGenericParameterType(VoidCommand.class));
		assertEquals(Void.class, ReflectionUtils.getCommandGenericParameterType(VoidCommand.class)); //cache test
		assertEquals(String.class, ReflectionUtils.getCommandGenericParameterType(StringCommand.class));

		Command<Integer> integerC = new Command<Integer>() {

			@Override
			public void execute(Integer parameter) {
			}
		};
		assertEquals(Integer.class,
				ReflectionUtils.getCommandGenericParameterType((Class<? extends Command<?>>) integerC.getClass()));

		Command<Float> floatC = f -> {
		};

		assertEquals(Object.class,
				ReflectionUtils.getCommandGenericParameterType((Class<? extends Command<?>>) floatC.getClass()));

		assertEquals(Object.class,
				ReflectionUtils.getCommandGenericParameterType((Class<? extends Command<?>>) GenericCommand.class));

		assertEquals(Object.class,
				ReflectionUtils.getCommandGenericParameterType((Class<? extends Command<?>>) RawCommand.class));

		assertEquals(Double.class, ReflectionUtils.getCommandGenericParameterType(GenericChildCommand.class));
	}

	private static class VoidCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}

	private static class StringCommand implements Command<String> {
		@Override
		public void execute(String parameter) {
		}
	}

	@SuppressWarnings("rawtypes")
	private static class RawCommand implements Command {
		@Override
		public void execute(Object parameter) {
		}
	}

	private static class GenericCommand<X> implements Command<X> {
		@Override
		public void execute(X parameter) {
		}
	}

	private static class GenericChildCommand extends GenericCommand<Double> {
		@Override
		public void execute(Double parameter) {
			super.execute(parameter);
		}

		@SuppressWarnings("unused")
		public void execute(Integer obj) {
		}

	}

}

package io.github.suice.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

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
	}

}

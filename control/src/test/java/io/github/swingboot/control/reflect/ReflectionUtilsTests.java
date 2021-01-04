package io.github.swingboot.control.reflect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

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
}

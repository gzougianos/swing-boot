package io.github.swingboot.control.listener;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;

import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.testutils.UiExtension;
import io.github.swingboot.testutils.UiTest;

@ExtendWith(UiExtension.class)
class ControlListenersTests {

	@UiTest
	void listenerExists() {
		JButton button = new JButton();
		Listener listener = new Listener();
		button.addActionListener(listener);
		assertEquals(listener, ControlListeners.get(ActionListener.class, button));

		Collection<ActionListener> all = ControlListeners.getAll(ActionListener.class, button);
		assertEquals(1, all.size());
		assertTrue(all.contains(listener));

		Listener listener2 = new Listener();
		button.addActionListener(listener2);
		all = ControlListeners.getAll(ActionListener.class, button);
		assertEquals(2, all.size());
		assertTrue(all.contains(listener2));
		assertTrue(all.contains(listener));
	}

	@UiTest
	void listenerDoesNotExist() {
		JButton button = new JButton();
		assertNull(ControlListeners.get(ActionListener.class, button));
		assertTrue(ControlListeners.getAll(ActionListener.class, button).isEmpty());
	}

	private static class Listener implements ActionListener, ControlListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		}

	}
}

package io.github.suice.control.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.junit.jupiter.api.extension.ExtendWith;

import testutils.UiExtension;
import testutils.UiTest;

@ExtendWith(UiExtension.class)
class ControlListenersTests {

	@UiTest
	void listenerExists() {
		JButton button = new JButton();
		Listener listener = new Listener();
		button.addActionListener(listener);
		assertEquals(listener, ControlListeners.get(ActionListener.class, button));
	}

	@UiTest
	void listenerDoesNotExist() {
		JButton button = new JButton();
		assertNull(ControlListeners.get(ActionListener.class, button));
	}

	private static class Listener implements ActionListener, ControlListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		}

	}
}

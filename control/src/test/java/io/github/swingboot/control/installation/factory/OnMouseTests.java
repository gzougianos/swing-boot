package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnMouseClicked;
import io.github.swingboot.control.installation.annotation.OnMouseEntered;
import io.github.swingboot.control.installation.annotation.OnMouseExited;
import io.github.swingboot.control.installation.annotation.OnMousePressed;
import io.github.swingboot.control.installation.annotation.OnMouseReleased;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnMouseTests {
	private static final int RANDOM_VALUE = 43423;

	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@Nested
	@ExtendWith(UiExtension.class)
	@UiAll
	class SpecificButton {
		@OnMouseReleased(value = TestControl.class, button = MouseEvent.BUTTON1)
		@OnMouseClicked(value = TestControl.class, button = MouseEvent.BUTTON1)
		@OnMouseEntered(value = TestControl.class, button = MouseEvent.BUTTON1)
		@OnMouseExited(value = TestControl.class, button = MouseEvent.BUTTON1)
		@OnMousePressed(value = TestControl.class, button = MouseEvent.BUTTON1)
		private JButton button = new JButton();

		@Test
		void match() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(RANDOM_VALUE, MouseEvent.BUTTON1, 55));
			verifyControlPerformed5Times();
		}

		@Test
		void mismatch() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(RANDOM_VALUE, MouseEvent.BUTTON2, 55));
			verifyControlNeverPerformed();
		}
	}

	@Nested
	@ExtendWith(UiExtension.class)
	@UiAll
	class SpecificClickCount {
		@OnMouseReleased(value = TestControl.class, clickCount = 4)
		@OnMouseClicked(value = TestControl.class, clickCount = 4)
		@OnMouseEntered(value = TestControl.class, clickCount = 4)
		@OnMouseExited(value = TestControl.class, clickCount = 4)
		@OnMousePressed(value = TestControl.class, clickCount = 4)
		private JButton button = new JButton();

		@Test
		void match() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(RANDOM_VALUE, MouseEvent.BUTTON1, 4));
			verifyControlPerformed5Times();
		}

		@Test
		void mismatch() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(RANDOM_VALUE, MouseEvent.BUTTON1, 5));
			verifyControlNeverPerformed();
		}
	}

	@Nested
	@ExtendWith(UiExtension.class)
	@UiAll
	class SpecificModifiers {
		@OnMouseReleased(value = TestControl.class, modifiers = Event.ALT_MASK)
		@OnMouseClicked(value = TestControl.class, modifiers = Event.ALT_MASK)
		@OnMouseEntered(value = TestControl.class, modifiers = Event.ALT_MASK)
		@OnMouseExited(value = TestControl.class, modifiers = Event.ALT_MASK)
		@OnMousePressed(value = TestControl.class, modifiers = Event.ALT_MASK)
		private JButton button = new JButton();

		@Test
		void match() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(Event.ALT_MASK, MouseEvent.BUTTON1, 43442423));
			verifyControlPerformed5Times();
		}

		@Test
		void mismatch() {
			installer.installTo(this);
			fireMouseListenersOf(button, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON1, 43442423));
			verifyControlNeverPerformed();
		}
	}

	@Nested
	@ExtendWith(UiExtension.class)
	@UiAll
	class Uninstallation {
		@OnMouseReleased(TestControl.class)
		@OnMouseClicked(TestControl.class)
		@OnMouseEntered(TestControl.class)
		@OnMouseExited(TestControl.class)
		@OnMousePressed(TestControl.class)
		private JButton button = new JButton();

		@Test
		void main() {
			installer.installTo(this);
			installer.uninstallFrom(this);
			fireMouseListenersOf(button, createEvent(Event.ALT_MASK, MouseEvent.BUTTON1, 43442423));
			verifyControlNeverPerformed();
		}

	}

	private void verifyControlNeverPerformed() {
		verifyNoInteractions(controls);
	}

	private void verifyControlPerformed5Times() {
		verify(controls, times(5)).perform(eq(TestControl.class));
	}

	private MouseEvent createEvent(int modifiers, int button, int clickCount) {
		return new MouseEvent(new JButton(), MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), modifiers,
				12, 12, 13, 13, clickCount, false, button);
	}

	private void fireMouseListenersOf(Component component, MouseEvent event) {
		Arrays.asList(component.getMouseListeners()).forEach(listener -> {
			listener.mouseClicked(event);
			listener.mouseEntered(event);
			listener.mouseExited(event);
			listener.mousePressed(event);
			listener.mouseReleased(event);
		});
	}

	private static class TestControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}
}

package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnMouseMoved;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

class OnMouseMotionTests {
	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@Nested
	@ExtendWith(UiExtension.class)
	@UiAll
	class OnMouseMovedTests {
		@OnMouseMoved(TestControl.class)
		private JButton noCondition = new JButton();

		@Test
		void noCondition() {
			fireMouseMotionListenersOf(noCondition, createEvent(111, MouseEvent.BUTTON1, 10));
			verify(controls).perform(eq(TestControl.class));
		}

		@OnMouseMoved(value = TestControl.class, button = MouseEvent.BUTTON2)
		private JButton specificButton = new JButton();

		@Test
		void specificButton() {
			fireMouseMotionListenersOf(specificButton, createEvent(111, MouseEvent.BUTTON1, 1));
			verifyNoInteractions(controls);

			fireMouseMotionListenersOf(specificButton, createEvent(111, MouseEvent.BUTTON2, 1));
			verify(controls).perform(eq(TestControl.class));
		}

		@OnMouseMoved(value = TestControl.class, clickCount = 2)
		private JButton specificClickCount = new JButton();

		@Test
		void clickCount() {
			fireMouseMotionListenersOf(specificClickCount, createEvent(111, MouseEvent.BUTTON1, 1));
			verifyNoInteractions(controls);

			fireMouseMotionListenersOf(specificClickCount, createEvent(111, MouseEvent.BUTTON1, 2));
			verify(controls).perform(eq(TestControl.class));
		}

		@OnMouseMoved(value = TestControl.class, modifiers = Event.ALT_MASK)
		private JButton specificModifier = new JButton();

		@Test
		void specificModifier() {
			fireMouseMotionListenersOf(specificModifier, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON1, 1));
			verifyNoInteractions(controls);

			fireMouseMotionListenersOf(specificModifier, createEvent(Event.ALT_MASK, MouseEvent.BUTTON1, 1));
			verify(controls).perform(eq(TestControl.class));
		}

		//@formatter:off eclipse why can't u format this?
		@OnMouseMoved(value = TestControl.class, 
				modifiers = Event.ALT_MASK,
				clickCount = 2, 
				button = MouseEvent.BUTTON2)
		//@formatter:on
		private JButton allSpecific = new JButton();

		@Test
		void allSpecific() {
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON1, 1));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON1, 2));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON2, 2));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.CTRL_MASK, MouseEvent.BUTTON2, 1));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.ALT_MASK, MouseEvent.BUTTON2, 1));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.ALT_MASK, MouseEvent.BUTTON1, 2));
			fireMouseMotionListenersOf(allSpecific, createEvent(Event.ALT_MASK, MouseEvent.BUTTON1, 1));
			verifyNoInteractions(controls);

			fireMouseMotionListenersOf(allSpecific, createEvent(Event.ALT_MASK, MouseEvent.BUTTON2, 2));
			verify(controls).perform(eq(TestControl.class));
		}

		@BeforeEach
		void init() {
			installer.installTo(this);
		}

	}

	private MouseEvent createEvent(int modifiers, int button, int clickCount) {
		return new MouseEvent(new JButton(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), modifiers,
				12, 12, clickCount, false, button);
	}

	private void fireMouseMotionListenersOf(Component component, MouseEvent event) {
		Arrays.asList(component.getMouseMotionListeners()).forEach(listener -> {
			listener.mouseDragged(event);
			listener.mouseMoved(event);
		});
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}
	}
}

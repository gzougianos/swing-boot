package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnKeyPressed;
import io.github.swingboot.control.installation.annotation.OnKeyReleased;
import io.github.swingboot.control.installation.annotation.OnKeyTyped;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnKeyTests {
	private static final int EVENT_KEY_CODE = KeyEvent.VK_0;
	private static final int EVENT_MODIFIERS = Event.ALT_MASK | Event.CTRL_MASK;

	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@OnKeyReleased(TestControl.class)
	@OnKeyTyped(TestControl.class)
	@OnKeyPressed(TestControl.class)
	private JButton plain = new JButton();

	@OnKeyReleased(value = TestControl.class, keyCode = EVENT_KEY_CODE)
	@OnKeyTyped(value = TestControl.class, keyCode = EVENT_KEY_CODE)
	@OnKeyPressed(value = TestControl.class, keyCode = EVENT_KEY_CODE)
	private JButton withKeyCodeMatch = new JButton();

	@OnKeyReleased(value = TestControl.class, keyCode = KeyEvent.VK_1)
	@OnKeyTyped(value = TestControl.class, keyCode = KeyEvent.VK_1)
	@OnKeyPressed(value = TestControl.class, keyCode = KeyEvent.VK_1)
	private JButton withKeyCodeMismatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = EVENT_MODIFIERS)
	@OnKeyTyped(value = TestControl.class, modifiers = EVENT_MODIFIERS)
	@OnKeyPressed(value = TestControl.class, modifiers = EVENT_MODIFIERS)
	private JButton withModifierMatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = Event.CTRL_MASK | Event.SHIFT_MASK)
	@OnKeyTyped(value = TestControl.class, modifiers = Event.CTRL_MASK | Event.SHIFT_MASK)
	@OnKeyPressed(value = TestControl.class, modifiers = Event.CTRL_MASK | Event.SHIFT_MASK)
	private JButton withModifierMismatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = EVENT_MODIFIERS, keyCode = EVENT_KEY_CODE)
	@OnKeyTyped(value = TestControl.class, modifiers = EVENT_MODIFIERS, keyCode = EVENT_KEY_CODE)
	@OnKeyPressed(value = TestControl.class, modifiers = EVENT_MODIFIERS, keyCode = EVENT_KEY_CODE)
	private JButton withModifierAndKeyCode = new JButton();

	@BeforeEach
	void install() {
		installer.installTo(this);
	}

	@Test
	void anyModifierAnyKeyCode() {
		fireKeyListenersOf(plain, createEvent(1111111, 234848));
		verifyEventConsumed();
	}

	@Test
	void specificModifierAnyKeyCode() {
		fireKeyListenersOf(withModifierMatch, createEvent(EVENT_MODIFIERS, 234848));
		verifyEventConsumed();
	}

	@Test
	void specificModifierMismatchAnyKeyCode() {
		fireKeyListenersOf(withModifierMismatch, createEvent(EVENT_MODIFIERS, 234848));
		verifyEventNotConsumed();
	}

	@Test
	void specificKeyCodeAnyModifier() {
		fireKeyListenersOf(withKeyCodeMatch, createEvent(426666, EVENT_KEY_CODE));
		verifyEventConsumed();
	}

	@Test
	void specificKeyCodeMisMatchAnyModifier() {
		fireKeyListenersOf(withKeyCodeMismatch, createEvent(426666, EVENT_KEY_CODE));
		verifyEventNotConsumed();
	}

	@Test
	void specificModifierSpecificKeyCode() {
		fireKeyListenersOf(withModifierAndKeyCode, createEvent(EVENT_MODIFIERS, EVENT_KEY_CODE));
		verifyEventConsumed();
	}

	@Test
	void uninstall() {
		installer.uninstallFrom(this);
		fireKeyListenersOf(plain, createEvent(1111111, 234848));
		verifyEventNotConsumed();
	}

	private void verifyEventNotConsumed() {
		verifyNoInteractions(controls);
	}

	private void verifyEventConsumed() {
		verify(controls, times(3)).perform(eq(TestControl.class));
	}

	private KeyEvent createEvent(int modifiers, int keyCode) {
		return new KeyEvent(plain, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + 200, modifiers,
				keyCode, '0');
	}

	private void fireKeyListenersOf(Component component, KeyEvent event) {
		Arrays.asList(component.getKeyListeners()).forEach(listener -> {
			listener.keyReleased(event);
			listener.keyTyped(event);
			listener.keyPressed(event);
		});
	}

	private static class TestControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}
}

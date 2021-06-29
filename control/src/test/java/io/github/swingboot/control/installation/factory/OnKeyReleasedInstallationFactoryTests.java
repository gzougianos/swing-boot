package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.OnKeyReleased;
import io.github.swingboot.testutils.UiAll;

@SuppressWarnings("unchecked")
@UiAll
class OnKeyReleasedInstallationFactoryTests {

	private static final char EVENT_KEY_CHAR = '0';
	private static final int EVENT_KEY_CODE = KeyEvent.VK_0;
	private static final int EVENT_MODIFIERS = Event.ALT_MASK | Event.CTRL_MASK;

	@OnKeyReleased(TestControl.class)
	private JButton plain = new JButton();

	@OnKeyReleased(value = TestControl.class, keyCode = EVENT_KEY_CODE)
	private JButton withKeyCodeMatch = new JButton();

	@OnKeyReleased(value = TestControl.class, keyCode = KeyEvent.VK_1)
	private JButton withKeyCodeMismatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = EVENT_MODIFIERS)
	private JButton withModifierMatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = Event.CTRL_MASK | Event.SHIFT_MASK)
	private JButton withModifierMismatch = new JButton();

	@OnKeyReleased(value = TestControl.class, modifiers = EVENT_MODIFIERS, keyCode = EVENT_KEY_CODE)
	private JButton withModifierAndKeyCode = new JButton();

	private Consumer<EventObject> eventConsumer = mock(Consumer.class);

	@Test
	void withoutModifiersWithoutKeyCode() throws Exception {
		assertEventConsumedFrom("plain", plain);
	}

	@Test
	void withKeyCode() throws Exception {
		assertEventConsumedFrom("withKeyCodeMatch", withKeyCodeMatch);
	}

	@Test
	void withKeyCodeMismatch() throws Exception {
		assertEventNotConsumedFrom("withKeyCodeMismatch", withKeyCodeMismatch);
	}

	@Test
	void withModifier() throws Exception {
		assertEventConsumedFrom("withModifierMatch", withModifierMatch);
	}

	@Test
	void withModifierMismatch() throws Exception {
		assertEventNotConsumedFrom("withModifierMismatch", withModifierMismatch);
	}

	@Test
	void withModifierAndCode() throws Exception {
		assertEventConsumedFrom("withModifierAndKeyCode", withModifierAndKeyCode);
	}

	@Test
	void uninstall() throws Exception {
		Installation installedInstallation = installOnField("plain");

		installedInstallation.uninstall();

		fireKeyEventsOf(plain);

		verifyNoInteractions(eventConsumer);
	}

	void assertEventConsumedFrom(String fieldName, Component component) throws Exception {
		installOnField(fieldName);
		fireKeyEventsOf(component);

		verify(eventConsumer).accept(any(KeyEvent.class));
	}

	void assertEventNotConsumedFrom(String fieldName, Component component) throws Exception {
		installOnField(fieldName);
		fireKeyEventsOf(component);

		verifyNoInteractions(eventConsumer);
	}

	private void fireKeyEventsOf(Component c) {
		Arrays.asList(c.getKeyListeners()).forEach(l -> {
			l.keyReleased(createEvent(c));
		});
	}

	private KeyEvent createEvent(Component c) {
		return new KeyEvent(c, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), EVENT_MODIFIERS,
				EVENT_KEY_CODE, EVENT_KEY_CHAR);
	}

	Installation installOnField(String fieldName) throws Exception {
		Annotation annotation = this.getClass().getDeclaredField(fieldName)
				.getAnnotation(OnKeyReleased.class);

		Object o = this.getClass().getDeclaredField(fieldName).get(this);
		Installation installation = new OnKeyReleasedInstallationFactory()
				.create(new InstallationContext(this, o, annotation, eventConsumer));
		installation.install();
		return installation;
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

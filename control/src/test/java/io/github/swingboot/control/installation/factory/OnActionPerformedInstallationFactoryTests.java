package io.github.swingboot.control.installation.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnActionPerformedInstallationFactoryTests {

	@OnActionPerformed(TestControl.class)
	private int anyModifierField;

	@OnActionPerformed(value = TestControl.class, modifiers = ActionEvent.ALT_MASK)
	private int specificModifierField;

	private Consumer<EventObject> eventConsumer;
	private JButton button;
	private OnActionPerformedInstallationFactory factory;

	@Test
	void anyModifier() throws Exception {
		ControlInstallation installation = factory.createInstallation(annotationOfField("anyModifierField"),
				button, eventConsumer);
		installation.install();

		fireActionListeners(button, 0);
		verify(eventConsumer).accept(isA(ActionEvent.class));

		fireActionListeners(button, 1);
		verify(eventConsumer, times(2)).accept(isA(ActionEvent.class));
		verifyNoMoreInteractions(eventConsumer);

		installation.uninstall();
		fireActionListeners(button, 1);
		verifyNoMoreInteractions(eventConsumer);
	}

	@Test
	void specificModifier() throws Exception {
		ControlInstallation installation = factory
				.createInstallation(annotationOfField("specificModifierField"), button, eventConsumer);
		installation.install();

		fireActionListeners(button, 0);
		verify(eventConsumer, never()).accept(any());

		fireActionListeners(button, ActionEvent.ALT_MASK);
		verify(eventConsumer).accept(isA(ActionEvent.class));
		verifyNoMoreInteractions(eventConsumer);

		installation.uninstall();
		fireActionListeners(button, ActionEvent.ALT_MASK);
		verifyNoMoreInteractions(eventConsumer);
	}

	@Test
	void wrongComponent() throws Exception {
		assertThrows(RuntimeException.class, () -> factory
				.createInstallation(annotationOfField("specificModifierField"), new JPanel(), eventConsumer));
	}

	private OnActionPerformed annotationOfField(String name) throws NoSuchFieldException {
		return this.getClass().getDeclaredField(name).getAnnotation(OnActionPerformed.class);
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	private void extracted() {
		factory = new OnActionPerformedInstallationFactory();
		button = new JButton();
		eventConsumer = mock(Consumer.class);
	}

	private void fireActionListeners(AbstractButton button, int eventModifiers) {
		ActionEvent event = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "cmd", eventModifiers);
		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(event));
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

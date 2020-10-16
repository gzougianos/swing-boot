package io.github.suice.command.annotation.installer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.annotation.OnActionPerformed;

class OnActionPerformedResolverTests extends AnnotationInstallerTestBase {

	@OnActionPerformed(TestCommand.class)
	private int noModifier;

	@OnActionPerformed(value = TestCommand.class, modifiers = ActionEvent.CTRL_MASK)
	private int withModifier;

	@OnActionPerformed(CorrectEventParameterizedCommand.class)
	private int correctPatameterized;

	@OnActionPerformed(IncorrectEventParameterizedCommand.class)
	private int incorrectPatameterized;

	private OnActionPerformedResolver installer;
	private CommandExecutor executor;

	@Test
	void anyModifier() throws NoSuchFieldException, SecurityException {
		JButton button = new JButton();
		installer.install(button, annotationOfField("noModifier"));

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, 0)));

		verify(executor).execute(eq(TestCommand.class));

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, 1)));

		verify(executor, times(2)).execute(eq(TestCommand.class));

		verifyNoMoreInteractions(executor);

		assertThrows(IllegalArgumentException.class, () -> installer.install(new JPanel(), annotationOfField("noModifier")));
	}

	@Test
	void specificModifier() throws Exception {
		JButton button = new JButton();
		installer.install(button, annotationOfField("withModifier"));

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, 800)));

		verify(executor, never()).execute(any());

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, ActionEvent.CTRL_MASK)));

		verify(executor).execute(eq(TestCommand.class));
		verifyNoMoreInteractions(executor);
	}

	@Test
	void validPatameterized() throws Exception {
		JButton button = new JButton();
		installer.install(button, annotationOfField("correctPatameterized"));

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, 800)));

		verify(executor).execute(eq(CorrectEventParameterizedCommand.class), isA(ActionEvent.class));
		verifyNoMoreInteractions(executor);
	}

	@Test
	void invalidPatameterized() throws Exception {
		JButton button = new JButton();
		installer.install(button, annotationOfField("incorrectPatameterized"));

		Arrays.asList(button.getActionListeners()).forEach(l -> l.actionPerformed(createEvent(button, 800)));

		verify(executor).execute(eq(IncorrectEventParameterizedCommand.class));
		verifyNoMoreInteractions(executor);
	}

	private ActionEvent createEvent(AbstractButton button, int modifier) {
		return new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "cmd", System.currentTimeMillis(), modifier);
	}

	@BeforeEach
	void init() {
		executor = mock(CommandExecutor.class);
		installer = new OnActionPerformedResolver(executor);
	}

	private static class TestCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}

	private static class CorrectEventParameterizedCommand implements Command<ActionEvent> {

		@Override
		public void execute(Optional<ActionEvent> parameter) {

		}

	}

	private static class IncorrectEventParameterizedCommand implements Command<ComponentEvent> {
		@Override
		public void execute(Optional<ComponentEvent> parameter) {

		}
	}
}

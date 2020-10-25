package io.github.suice.command;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

public class EventParameterAwareExecutorTests {

	@Test
	void test() {
		CommandExecutor executor = mock(CommandExecutor.class);

		ActionEvent event = new ActionEvent(new JButton(), 0, "");

		new CommandDeclarationExecutor(executor, WithoutParametersCommand.class).execute(event);
		verify(executor).execute(eq(WithoutParametersCommand.class));

		new CommandDeclarationExecutor(executor, ParameterMismatchCommand.class).execute(event);
		verify(executor).execute(eq(ParameterMismatchCommand.class));

		new CommandDeclarationExecutor(executor, ParameterCommand.class).execute(event);
		verify(executor).execute(eq(ParameterCommand.class), eq(event));

		new CommandDeclarationExecutor(executor, AwtParameterCommand.class).execute(event);
		verify(executor).execute(eq(AwtParameterCommand.class), eq(event));

		verifyNoMoreInteractions(executor);
	}

	private static class WithoutParametersCommand implements Command<Void> {

		@Override
		public void execute(Void parameter) {
		}

	}

	private static class ParameterMismatchCommand implements Command<ComponentEvent> {

		@Override
		public void execute(ComponentEvent parameter) {
		}

	}

	private static class ParameterCommand implements Command<ActionEvent> {

		@Override
		public void execute(ActionEvent parameter) {
		}

	}

	private static class AwtParameterCommand implements Command<AWTEvent> {

		@Override
		public void execute(AWTEvent parameter) {
		}

	}

}

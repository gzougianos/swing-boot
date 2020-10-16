package io.github.suice.command;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.Optional;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public class EventParameterAndOrderAwareExecutorTests {

	@Test
	void test() {
		@SuppressWarnings("unchecked")
		Class<? extends Command<?>>[] cmdTypes = new Class[] { WithoutParametersCommand.class, ParameterMismatchCommand.class,
				ParameterCommand.class, AwtParameterCommand.class };

		CommandExecutor executor = mock(CommandExecutor.class);

		ActionEvent event = new ActionEvent(new JButton(), 0, "");
		new EventParameterAndOrderAwareExecutor(executor, cmdTypes).execute(event);

		InOrder inOrder = inOrder(executor);

		inOrder.verify(executor, times(1)).execute(eq(WithoutParametersCommand.class));
		inOrder.verify(executor, times(1)).execute(eq(ParameterMismatchCommand.class));
		inOrder.verify(executor, times(1)).execute(eq(ParameterCommand.class), eq(event));
		inOrder.verify(executor, times(1)).execute(eq(AwtParameterCommand.class), eq(event));

		inOrder.verifyNoMoreInteractions();
	}

	private static class WithoutParametersCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}

	private static class ParameterMismatchCommand implements Command<ComponentEvent> {

		@Override
		public void execute(Optional<ComponentEvent> parameter) {
		}

	}

	private static class ParameterCommand implements Command<ActionEvent> {

		@Override
		public void execute(Optional<ActionEvent> parameter) {
		}

	}

	private static class AwtParameterCommand implements Command<AWTEvent> {

		@Override
		public void execute(Optional<AWTEvent> parameter) {
		}

	}

}

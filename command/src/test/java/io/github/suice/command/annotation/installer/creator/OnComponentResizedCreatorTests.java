package io.github.suice.command.annotation.installer.creator;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.ComponentEvent;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.annotation.OnComponentResized;
import io.github.suice.command.annotation.installer.ReflectionTestUtils;

class OnComponentResizedCreatorTests implements ReflectionTestUtils {

	@OnComponentResized(TestCommand.class)
	private int field;

	private OnComponentResizedCreator creator;
	private CommandExecutor executor;

	@Test
	void main() throws Exception {
		JButton button = new JButton();
		button.addComponentListener(creator.createListener((OnComponentResized) annotationOfField("field")));

		fireAllListeners(button);

		verify(executor).execute(eq(TestCommand.class));

		verifyNoMoreInteractions(executor);

	}

	private void fireAllListeners(JButton button) {
		Arrays.asList(button.getComponentListeners()).forEach(l -> l.componentResized(createEvent(button)));
	}

	private ComponentEvent createEvent(AbstractButton button) {
		return new ComponentEvent(button, ComponentEvent.COMPONENT_RESIZED);
	}

	@BeforeEach
	void init() {
		executor = mock(CommandExecutor.class);
		creator = new OnComponentResizedCreator(executor);
	}

	private static class TestCommand implements Command<Void> {

		@Override
		public void execute(Void parameter) {
		}

	}

}

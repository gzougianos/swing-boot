package io.github.suice.command.annotation.installer;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.command.Command;
import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.installer.creator.ListenerCreator;

class ListenerDirectlyToComponentAnnotationInstallerTests implements AnnotationInstallerTestUtils {
	@OnActionPerformed(TestCommand.class)
	private int field;

	@SuppressWarnings("unchecked")
	@Test
	void main() throws Exception {
		ListenerCreator<OnActionPerformed, ActionListener> creator = mock(ListenerCreator.class);
		ActionListener listener = e -> {
		};
		when(creator.createListener(any())).thenReturn(listener);

		ComponentAnnotationInstaller resolver = new ListenerDirectlyToComponentAnnotationInstaller<>(OnActionPerformed.class, "addActionListener",
				ActionListener.class, creator);

		assertThrows(IllegalArgumentException.class, () -> resolver.install(new JPanel(), annotationOfField("field")));

		JButton button = new JButton();

		resolver.install(button, annotationOfField("field"));

		assertTrue(Arrays.asList(button.getActionListeners()).contains(listener));
	}

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}
}

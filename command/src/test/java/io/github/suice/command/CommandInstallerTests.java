package io.github.suice.command;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;

class CommandInstallerTests {
	private static boolean additionalsInstalled = false;
	private CommandInstaller installer;
	private CommandExecutor executor;

	@Test
	void main() {
		EverythingOk obj = new EverythingOk();
		installer.installCommands(obj);
		installer.installCommands(obj); //try to install again
		obj.button.doClick();

		verify(executor).execute(TestCommand.class);
		verifyNoMoreInteractions(executor);
		assertTrue(additionalsInstalled);
	}

	@Test
	void nullComponentToField() {
		NullComponent nullComponent = new NullComponent();
		assertThrows(NullPointerException.class, () -> installer.installCommands(nullComponent));
	}

	@BeforeEach
	private void extracted() {
		additionalsInstalled = false;
		executor = mock(CommandExecutor.class);
		installer = new CommandInstaller(executor);
	}

	@InstallCommands
	private static class EverythingOk implements AdditionalCommandInstallation {

		@OnActionPerformed(TestCommand.class)
		private JButton button = new JButton();

		@Override
		public void installCommands(CommandExecutor commandExecutor) {
			additionalsInstalled = true;
		}
	}

	@InstallCommands
	private static class NullComponent {
		@OnActionPerformed(TestCommand.class)
		private JButton button;

	}

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}

	}
}

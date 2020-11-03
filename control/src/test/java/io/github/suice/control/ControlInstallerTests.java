package io.github.suice.control;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.listener.OnActionPerformed;

class ControlInstallerTests {
	private static boolean additionalsInstalled = false;
	private ControlInstaller installer;
	private Controls executor;

	@Test
	void main() {
		EverythingOk obj = new EverythingOk();
		installer.installControls(obj);
		installer.installControls(obj); //try to install again
		obj.button.doClick();

		verify(executor).perform(TestControl.class);
		verifyNoMoreInteractions(executor);
		assertTrue(additionalsInstalled);
	}

	@Test
	void nullComponentToField() {
		NullComponent nullComponent = new NullComponent();
		assertThrows(NullPointerException.class, () -> installer.installControls(nullComponent));
	}

	@BeforeEach
	private void extracted() {
		additionalsInstalled = false;
		executor = mock(Controls.class);
		installer = new ControlInstaller(executor);
	}

	@InstallControls
	private static class EverythingOk implements AdditionalControlInstallation {

		@OnActionPerformed(TestControl.class)
		private JButton button = new JButton();

		@Override
		public void installAdditionalControls(Controls controls) {
			additionalsInstalled = true;
		}
	}

	@InstallControls
	private static class NullComponent {
		@OnActionPerformed(TestControl.class)
		private JButton button;

	}

	private static class TestControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}

	}
}

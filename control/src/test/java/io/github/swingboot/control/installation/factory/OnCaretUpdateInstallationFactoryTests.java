package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnCaretUpdate;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnCaretUpdateInstallationFactoryTests {
	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@OnCaretUpdate(TestControl.class)
	private JTextField textField = new JTextField("blabla");

	@Test
	void main() {
		textField.setCaretPosition(1);

		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void uninstall() {
		installer.uninstallFrom(this);
		verifyNoInteractions(controls);
	}

	@BeforeEach
	void init() {
		installer.installTo(this);
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}
	}
}

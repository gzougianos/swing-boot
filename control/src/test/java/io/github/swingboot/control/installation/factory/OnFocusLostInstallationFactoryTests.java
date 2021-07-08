package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.FocusEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnFocusLost;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnFocusLostInstallationFactoryTests {

	@OnFocusLost(TestControl.class)
	private JButton button = new JButton();

	@Test
	void main() {
		Controls controls = mock(Controls.class);
		ControlInstaller installer = new ControlInstaller(controls);
		installer.installTo(this);

		fireFocusLostListeners();

		verify(controls).perform(eq(TestControl.class));

		installer.uninstallFrom(this);
		fireFocusLostListeners();

		verifyNoMoreInteractions(controls);
	}

	private void fireFocusLostListeners() {
		Arrays.asList(button.getFocusListeners()).forEach(listener -> {
			listener.focusLost(new FocusEvent(button, FocusEvent.FOCUS_GAINED));
		});
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}
	}
}

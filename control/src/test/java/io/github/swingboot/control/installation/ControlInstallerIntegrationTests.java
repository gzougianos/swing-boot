package io.github.swingboot.control.installation;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.ParameterSource;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.testutils.UiExtension;
import io.github.swingboot.testutils.UiTest;

@ExtendWith(UiExtension.class)
public class ControlInstallerIntegrationTests {

	@Nested
	@InstallControls
	class NoParameterSource {
		@OnActionPerformed(VoidControl.class)
		private JButton button = new JButton();

		@UiTest
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installTo(this);
			button.doClick();
			verify(controls).perform(eq(VoidControl.class));
			verifyNoMoreInteractions(controls);
		}
	}

	@Nested
	@InstallControls
	class NoEventParameterSource {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton button = new JButton();

		@UiTest
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installTo(this);
			button.doClick();
			verify(controls).perform(eq(IntControl.class), eq(5));
			verifyNoMoreInteractions(controls);
		}

		@ParameterSource("parsource")
		private int parSource() {
			return 5;
		}
	}

	@Nested
	@InstallControls
	class WithCastableEventParameterSource {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton button = new JButton();

		@UiTest
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installTo(this);
			button.doClick();
			verify(controls).perform(eq(IntControl.class), eq(7));
			verifyNoMoreInteractions(controls);
		}

		@ParameterSource("parsource")
		private int parSource(ActionEvent event) {
			assertNotNull(event);
			return 7;
		}
	}

	@Nested
	@InstallControls
	class WithNonCastableEventParameterSource {
		@OnActionPerformed(value = IntControl.class, parameterSource = "parsource")
		private JButton button = new JButton();

		@UiTest
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installTo(this);
			button.doClick();
			verify(controls).perform(eq(IntControl.class), eq(10));
			verifyNoMoreInteractions(controls);
		}

		@ParameterSource("parsource")
		private int parSource(ComponentEvent event) {
			assertNull(event);
			return 10;
		}
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}

	private static class IntControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}

}

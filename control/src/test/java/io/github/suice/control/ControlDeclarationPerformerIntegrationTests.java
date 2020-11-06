package io.github.suice.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.ParameterSource;
import io.github.suice.control.annotation.listener.OnActionPerformed;

public class ControlDeclarationPerformerIntegrationTests {

	@Nested
	@InstallControls
	class NoParameterSource {
		@OnActionPerformed(VoidControl.class)
		private JButton button = new JButton();

		@Test
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installControls(this);
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

		@Test
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installControls(this);
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

		@Test
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installControls(this);
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

		@Test
		void main() {
			Controls controls = mock(Controls.class);
			new ControlInstaller(controls).installControls(this);
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

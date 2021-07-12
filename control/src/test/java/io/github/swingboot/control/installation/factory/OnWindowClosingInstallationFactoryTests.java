package io.github.swingboot.control.installation.factory;

import static java.awt.event.WindowEvent.WINDOW_CLOSING;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnWindowClosing;
import io.github.swingboot.control.installation.annotation.WindowState;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnWindowClosingInstallationFactoryTests {
	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@OnWindowClosing(value = TestControl.class)
	private JDialog anyState = new JDialog();

	@OnWindowClosing(value = TestControl.class, newState = WindowState.ICONIFIED)
	private JDialog specificNewState = new JDialog();

	@OnWindowClosing(value = TestControl.class, oldState = WindowState.ICONIFIED)
	private JDialog specificOldState = new JDialog();

	@OnWindowClosing(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	private JDialog specificBothStates = new JDialog();

	@Test
	void anyState() {
		dispatchTo(anyState, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void specificNewState() {
		dispatchTo(specificNewState, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verifyNoInteractions(controls);

		dispatchTo(specificNewState, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void specificOldState() {
		dispatchTo(specificOldState, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		verifyNoInteractions(controls);

		dispatchTo(specificOldState, WINDOW_CLOSING, Frame.ICONIFIED, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void specificBothStates() {
		dispatchTo(specificBothStates, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		dispatchTo(specificBothStates, WINDOW_CLOSING, Frame.ICONIFIED, Frame.ICONIFIED);
		dispatchTo(specificBothStates, WINDOW_CLOSING, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verifyNoInteractions(controls);

		dispatchTo(specificBothStates, WINDOW_CLOSING, Frame.ICONIFIED, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	void dispatchTo(Window w, int id, int oldState, int newState) {
		w.dispatchEvent(new WindowEvent(w, id, oldState, newState));
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

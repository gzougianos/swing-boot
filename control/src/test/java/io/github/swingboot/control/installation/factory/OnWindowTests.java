package io.github.swingboot.control.installation.factory;

import static java.awt.event.WindowEvent.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javax.swing.JDialog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnWindowActivated;
import io.github.swingboot.control.installation.annotation.OnWindowClosed;
import io.github.swingboot.control.installation.annotation.OnWindowClosing;
import io.github.swingboot.control.installation.annotation.OnWindowDeactivated;
import io.github.swingboot.control.installation.annotation.OnWindowDeiconified;
import io.github.swingboot.control.installation.annotation.WindowState;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnWindowTests {
	private static final Map<Integer, BiConsumer<WindowListener, WindowEvent>> IDS_TO_LISTENER_METHODS = new HashMap<>();
	static {
		IDS_TO_LISTENER_METHODS.put(WINDOW_ACTIVATED, WindowListener::windowActivated);
		IDS_TO_LISTENER_METHODS.put(WINDOW_CLOSING, WindowListener::windowClosing);
		IDS_TO_LISTENER_METHODS.put(WINDOW_DEACTIVATED, WindowListener::windowDeactivated);
		IDS_TO_LISTENER_METHODS.put(WINDOW_CLOSED, WindowListener::windowClosed);
		IDS_TO_LISTENER_METHODS.put(WINDOW_DEICONIFIED, WindowListener::windowDeiconified);
	}
	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@OnWindowClosing(value = TestControl.class)
	@OnWindowActivated(value = TestControl.class)
	@OnWindowDeactivated(value = TestControl.class)
	@OnWindowClosed(value = TestControl.class)
	@OnWindowDeiconified(value = TestControl.class)
	private JDialog anyState = new JDialog();

	@ParameterizedTest
	@MethodSource("provideIds")
	void anyState(int id) {
		dispatchTo(anyState, id, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	@OnWindowClosing(value = TestControl.class, newState = WindowState.ICONIFIED)
	@OnWindowActivated(value = TestControl.class, newState = WindowState.ICONIFIED)
	@OnWindowDeactivated(value = TestControl.class, newState = WindowState.ICONIFIED)
	@OnWindowClosed(value = TestControl.class, newState = WindowState.ICONIFIED)
	@OnWindowDeiconified(value = TestControl.class, newState = WindowState.ICONIFIED)
	private JDialog specificNewState = new JDialog();

	@ParameterizedTest
	@MethodSource("provideIds")
	void specificNewState(int id) {
		dispatchTo(specificNewState, id, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verifyNoInteractions(controls);

		dispatchTo(specificNewState, id, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		verify(controls).perform(eq(TestControl.class));
	}

	@OnWindowClosing(value = TestControl.class, oldState = WindowState.ICONIFIED)
	@OnWindowActivated(value = TestControl.class, oldState = WindowState.ICONIFIED)
	@OnWindowDeactivated(value = TestControl.class, oldState = WindowState.ICONIFIED)
	@OnWindowClosed(value = TestControl.class, oldState = WindowState.ICONIFIED)
	@OnWindowDeiconified(value = TestControl.class, oldState = WindowState.ICONIFIED)
	private JDialog specificOldState = new JDialog();

	@ParameterizedTest
	@MethodSource("provideIds")
	void specificOldState(int id) {
		dispatchTo(specificOldState, id, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		verifyNoInteractions(controls);

		dispatchTo(specificOldState, id, Frame.ICONIFIED, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	@OnWindowClosing(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	@OnWindowActivated(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	@OnWindowDeactivated(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	@OnWindowClosed(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	@OnWindowDeiconified(value = TestControl.class, oldState = WindowState.ICONIFIED, newState = WindowState.MAXIMIZED_BOTH)
	private JDialog specificBothStates = new JDialog();

	@ParameterizedTest()
	@MethodSource("provideIds")
	void specificBothStates(int id) {
		dispatchTo(specificBothStates, id, Frame.MAXIMIZED_BOTH, Frame.ICONIFIED);
		dispatchTo(specificBothStates, id, Frame.ICONIFIED, Frame.ICONIFIED);
		dispatchTo(specificBothStates, id, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verifyNoInteractions(controls);

		dispatchTo(specificBothStates, id, Frame.ICONIFIED, Frame.MAXIMIZED_BOTH);
		verify(controls).perform(eq(TestControl.class));
	}

	@ParameterizedTest
	@MethodSource("provideIds")
	void uninstall(int id) {
		installer.uninstallFrom(this);
		dispatchTo(anyState, id, Frame.MAXIMIZED_BOTH, Frame.MAXIMIZED_BOTH);
		verifyNoInteractions(controls);
	}

	void dispatchTo(Window w, int id, int oldState, int newState) {
		WindowEvent event = new WindowEvent(w, id, oldState, newState);
		Arrays.asList(w.getWindowListeners()).forEach(listener -> {
			IDS_TO_LISTENER_METHODS.get(id).accept(listener, event);
		});
	}

	static Stream<Integer> provideIds() {
		return IDS_TO_LISTENER_METHODS.keySet().stream();
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

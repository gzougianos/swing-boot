package io.github.suice.control;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.ParameterSource;
import io.github.suice.control.annotation.listener.OnActionPerformed;
import io.github.suice.control.annotation.listener.OnComponentResized;

@InstallControls
public class ControlDeclarationPerformerIntegrationTests {

	@OnActionPerformed(VoidControl.class)
	private JButton button = new JButton();

	@OnComponentResized(EventControl.class)
	@OnActionPerformed(EventControl.class)
	private JButton eventButton = new JButton();

	@OnActionPerformed(value = EventControl.class, parameterSource = "parsource")
	private JButton parameterSourcedButton = new JButton();

	@Test
	void noParameterSourceNoEventParameter() {
		Controls controls = mock(Controls.class);
		new ControlInstaller(controls).installControls(this);
		button.doClick();
		verify(controls).perform(eq(VoidControl.class));
		verifyNoMoreInteractions(controls);
	}

	@Test
	void castableEventParameter() {
		Controls controls = mock(Controls.class);
		new ControlInstaller(controls).installControls(this);
		eventButton.doClick();
		verify(controls).perform(eq(EventControl.class), isA(ActionEvent.class));
		verifyNoMoreInteractions(controls);
	}

	@Test
	void nonCastableEventParameter() {
		Controls controls = mock(Controls.class);
		new ControlInstaller(controls).installControls(this);
		Arrays.asList(eventButton.getComponentListeners())
				.forEach(l -> l.componentResized(new ComponentEvent(eventButton, ComponentEvent.COMPONENT_RESIZED)));
		verify(controls).perform(eq(EventControl.class));
		verifyNoMoreInteractions(controls);
	}

	@Test
	void withParameterSource() {
		Controls controls = mock(Controls.class);
		new ControlInstaller(controls).installControls(this);
		parameterSourcedButton.doClick();
		verify(controls).perform(eq(EventControl.class), isA(ActionEvent.class));
		verifyNoMoreInteractions(controls);
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}

	private static class EventControl implements Control<ActionEvent> {
		@Override
		public void perform(ActionEvent parameter) {
		}
	}

	@ParameterSource("parsource")
	private ActionEvent parSource(ActionEvent event) {
		return event;
	}
}

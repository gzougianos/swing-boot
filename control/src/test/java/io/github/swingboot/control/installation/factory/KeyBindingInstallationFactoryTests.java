package io.github.swingboot.control.installation.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.KeyBinding;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class KeyBindingInstallationFactoryTests {

	private Controls controls;

	@Nested
	@UiAll
	class ToJComponentDirectly {
		@Test
		void main() {
			View view = new View();
			ControlInstaller installer = new ControlInstaller(controls);
			installer.installControls(view);

			JPanel panel = view.panel;
			Object binding = panel.getInputMap(JComponent.WHEN_FOCUSED)
					.get(KeyStroke.getKeyStroke("released F2"));
			assertNotNull(binding);

			ActionEvent event = new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "cmd");
			panel.getActionMap().get("id").actionPerformed(event);

			verify(controls).perform(eq(TestControl.class));

			installer.uninstallFrom(view);

			binding = panel.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke("released F2"));
			assertNull(binding);
		}

		class View {
			@KeyBinding(value = TestControl.class, id = "id", keyStroke = "released F2")
			JPanel panel = new JPanel();
		}
	}

	@UiAll
	@Nested
	class ToContentPaneContainer {
		@Test
		void main() {
			View view = new View();
			ControlInstaller installer = new ControlInstaller(controls);
			installer.installControls(view);

			JFrame frame = view.frame;
			JPanel panel = (JPanel) frame.getContentPane();
			Object binding = panel.getInputMap(JComponent.WHEN_FOCUSED)
					.get(KeyStroke.getKeyStroke("released F2"));
			assertNotNull(binding);

			ActionEvent event = new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "cmd");
			panel.getActionMap().get("id").actionPerformed(event);

			verify(controls).perform(eq(TestControl.class));

			installer.uninstallFrom(view);

			binding = panel.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke("released F2"));
			assertNull(binding);
		}

		class View {
			@KeyBinding(value = TestControl.class, id = "id", keyStroke = "released F2")
			JFrame frame = new JFrame();
		}
	}

	@UiAll
	@Nested
	class ExceptionWhenContentPaneContainerIsNotJComponent {
		@Test
		void main() {
			View view = new View();
			ControlInstaller installer = new ControlInstaller(controls);
			assertThrows(RuntimeException.class, () -> installer.installControls(view));
		}

		class View {
			@KeyBinding(value = TestControl.class, id = "id", keyStroke = "released F2")
			JFrame frame = new JFrame();

			View() {
				frame.setContentPane(new Container());
			}
		}
	}

	@BeforeEach
	void init() throws Exception {
		controls = mock(Controls.class);
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}

	}
}

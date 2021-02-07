package io.github.swingboot.control.installation.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.KeyBinding;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class KeyBindingInstallationFactoryTests {

	@KeyBinding(value = TestControl.class, id = "id", keyStroke = "released F2")
	private int annotationHolder;

	private Consumer<EventObject> eventConsumer;
	private KeyBinding annotation;

	@Test
	void toJComponent() {
		JPanel panel = new JPanel();
		ControlInstallation installation = createInstallation(panel);
		installation.install();

		Object binding = panel.getInputMap(annotation.when())
				.get(KeyStroke.getKeyStroke(annotation.keyStroke()));
		assertNotNull(binding);

		ActionEvent event = new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "cmd");
		panel.getActionMap().get("id").actionPerformed(event);
		verify(eventConsumer).accept(eq(event));

		installation.uninstall();
		binding = panel.getInputMap(annotation.when()).get(KeyStroke.getKeyStroke(annotation.keyStroke()));
		assertNull(binding);
	}

	@Test
	void toContentPaneContainer() {
		JFrame frame = new JFrame();
		ControlInstallation installation = createInstallation(frame);
		installation.install();

		JComponent panel = (JComponent) frame.getContentPane();

		Object binding = panel.getInputMap(annotation.when())
				.get(KeyStroke.getKeyStroke(annotation.keyStroke()));
		assertNotNull(binding);

		ActionEvent event = new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "cmd");
		panel.getActionMap().get("id").actionPerformed(event);
		verify(eventConsumer).accept(eq(event));

		installation.uninstall();
		binding = panel.getInputMap(annotation.when()).get(KeyStroke.getKeyStroke(annotation.keyStroke()));
		assertNull(binding);
	}

	@Test
	void toRootContainerWithoutJComponentContainer() {
		JFrame frame = new JFrame();
		frame.setContentPane(new Container());

		assertThrows(RuntimeException.class, () -> createInstallation(frame));
	}

	@Test
	void notSupportedComponent() {
		assertThrows(RuntimeException.class, () -> createInstallation(new java.awt.Button()));
	}

	ControlInstallation createInstallation(Object target) {
		return new KeyBindingInstallationFactory()
				.createInstallation(new InstallationContext(this, target, annotation, eventConsumer));
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	void init() throws Exception {
		eventConsumer = mock(Consumer.class);
		annotation = getClass().getDeclaredField("annotationHolder").getAnnotation(KeyBinding.class);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

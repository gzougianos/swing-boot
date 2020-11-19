package io.github.suice.control.annotation.installer;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.suice.control.Control;
import io.github.suice.control.annotation.KeyBinding;
import testutils.UiAll;
import testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class KeyBindingInstallerTests {

	@KeyBinding(value = TestControl.class, id = "id", keyStroke = "released F2")
	private int annotationHolder;

	private AnnotationInstaller installer;
	private Consumer<AWTEvent> eventConsumer;
	private KeyBinding annotation;

	@Test
	void toJComponent() {
		JPanel panel = new JPanel();
		installer.installAnnotation(annotation, panel, eventConsumer);

		assertNotNull(panel.getInputMap(annotation.when()).get(KeyStroke.getKeyStroke(annotation.keyStroke())));

		ActionEvent event = new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "cmd");
		panel.getActionMap().get("id").actionPerformed(event);
		verify(eventConsumer).accept(eq(event));
	}

	@Test
	void toContentPaneContainer() {
		JFrame frame = new JFrame();
		installer.installAnnotation(annotation, frame, eventConsumer);

		JComponent panel = (JComponent) frame.getContentPane();
		assertNotNull(panel.getInputMap(annotation.when()).get(KeyStroke.getKeyStroke(annotation.keyStroke())));

		ActionEvent event = new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "cmd");
		panel.getActionMap().get("id").actionPerformed(event);
		verify(eventConsumer).accept(eq(event));
	}

	@Test
	void toRootContainerWithoutJComponentContainer() {
		JFrame frame = new JFrame();
		frame.setContentPane(new Container());

		assertThrows(UnsupportedOperationException.class, () -> installer.installAnnotation(annotation, frame, eventConsumer));
	}

	@Test
	void notSupportedComponent() {
		assertThrows(UnsupportedOperationException.class,
				() -> installer.installAnnotation(annotation, new Button(), eventConsumer));
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	void init() throws Exception {
		installer = new KeyBindingInstaller();
		eventConsumer = mock(Consumer.class);
		annotation = getClass().getDeclaredField("annotationHolder").getAnnotation(KeyBinding.class);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

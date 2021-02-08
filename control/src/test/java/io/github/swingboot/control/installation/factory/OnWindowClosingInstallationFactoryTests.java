package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.event.WindowEvent;
import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.OnWindowClosing;
import io.github.swingboot.control.installation.annotation.WindowState;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnWindowClosingInstallationFactoryTests {
	@OnWindowClosing(value = TestControl.class, newState = WindowState.ICONIFIED)
	private int field;
	private Consumer<EventObject> eventConsumer;

	@Test
	void test() throws Exception {
		JFrame frame = new JFrame();
		Installation installation = createInstallation("field", frame);

		installation.install();

		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING, JFrame.MAXIMIZED_BOTH,
				JFrame.MAXIMIZED_BOTH));
		verifyNoInteractions(eventConsumer);

		frame.dispatchEvent(
				new WindowEvent(frame, WindowEvent.WINDOW_CLOSING, JFrame.MAXIMIZED_BOTH, JFrame.ICONIFIED));
		verify(eventConsumer).accept(isA(WindowEvent.class));

		installation.uninstall();

		frame.dispatchEvent(
				new WindowEvent(frame, WindowEvent.WINDOW_CLOSING, JFrame.MAXIMIZED_BOTH, JFrame.ICONIFIED));
		verifyNoMoreInteractions(eventConsumer);
	}

	private Installation createInstallation(String fieldName, Object target) throws Exception {
		Annotation annotation = this.getClass().getDeclaredField(fieldName)
				.getAnnotation(OnWindowClosing.class);

		InstallationContext context = new InstallationContext(this, target, annotation, eventConsumer);
		return new OnWindowClosingInstallationFactory().create(context);
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	void init() {
		eventConsumer = mock(Consumer.class);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

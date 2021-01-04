package io.github.swingboot.control.annotation.installer;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.EventListener;
import java.util.function.Consumer;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.annotation.OnActionPerformed;
import io.github.swingboot.control.annotation.OnComponentResized;
import io.github.swingboot.control.listener.ControlListeners;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@SuppressWarnings("unchecked")
@UiAll
class OnComponentResizedInstallerTests {
	@OnComponentResized(TestControl.class)
	private int field;

	@Test
	void main() throws Exception {
		OnComponentResizedInstaller installer = new OnComponentResizedInstaller();
		JButton button = new JButton();
		Consumer<AWTEvent> eventConsumer = mock(Consumer.class);

		installer.installAnnotation(annotationOfField("field"), button, eventConsumer);
		assertNotNull(ControlListeners.get(ComponentListener.class, button));

		ComponentEvent event = new ComponentEvent(button, ComponentEvent.COMPONENT_RESIZED);
		fireListeners(button, ComponentListener.class, l -> l.componentResized(event));

		verify(eventConsumer).accept(eq(event));
		verifyNoMoreInteractions(eventConsumer);
	}

	@Test
	void wrongTarget() throws Exception {
		OnComponentResizedInstaller installer = new OnComponentResizedInstaller();
		Consumer<AWTEvent> eventConsumer = mock(Consumer.class);
		assertThrows(RuntimeException.class,
				() -> installer.installAnnotation(annotationOfField("field"), new String(), eventConsumer));
	}

	private <T extends EventListener> void fireListeners(Component c, Class<T> type,
			Consumer<T> listenerConsumer) {
		Arrays.asList(c.getListeners(type)).forEach(listenerConsumer);
	}

	private OnActionPerformed annotationOfField(String name) throws NoSuchFieldException {
		return this.getClass().getDeclaredField(name).getAnnotation(OnActionPerformed.class);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

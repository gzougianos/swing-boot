package io.github.swingboot.control.installation.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.control.installation.annotation.OnComponentResized;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@SuppressWarnings("unchecked")
@UiAll
class OnComponentResizedInstallationFactoryTests {
	@OnComponentResized(TestControl.class)
	private int field;
	private Consumer<EventObject> eventConsumer;

	@Test
	void main() throws Exception {
		JButton button = new JButton();

		Installation installation = createInstallation("field", button);
		installation.install();

		ComponentEvent event = new ComponentEvent(button, ComponentEvent.COMPONENT_RESIZED);
		fireListeners(button, ComponentListener.class, l -> l.componentResized(event));

		verify(eventConsumer).accept(eq(event));
		verifyNoMoreInteractions(eventConsumer);

		installation.uninstall();

		fireListeners(button, ComponentListener.class, l -> l.componentResized(event));
		verifyNoMoreInteractions(eventConsumer);
	}

	@Test
	void wrongTarget() throws Exception {
		assertThrows(RuntimeException.class, () -> createInstallation("field", new String()));
	}

	private <T extends EventListener> void fireListeners(Component c, Class<T> type,
			Consumer<T> listenerConsumer) {
		Arrays.asList(c.getListeners(type)).forEach(listenerConsumer);
	}

	private Installation createInstallation(String fieldName, Object target)
			throws NoSuchFieldException {
		OnActionPerformed annotation = getClass().getDeclaredField(fieldName)
				.getAnnotation(OnActionPerformed.class);

		InstallationContext context = new InstallationContext(this, target, annotation, eventConsumer);
		return new OnComponentResizedInstallationFactory().create(context);
	}

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

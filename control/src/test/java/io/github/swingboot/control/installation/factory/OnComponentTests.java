package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.function.BiConsumer;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnComponentHidden;
import io.github.swingboot.control.installation.annotation.OnComponentMoved;
import io.github.swingboot.control.installation.annotation.OnComponentResized;
import io.github.swingboot.control.installation.annotation.OnComponentShown;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnComponentTests {

	@OnComponentShown(TestControl.class)
	@OnComponentResized(TestControl.class)
	@OnComponentHidden(TestControl.class)
	@OnComponentMoved(TestControl.class)
	private JButton button = new JButton();

	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@BeforeEach
	void init() {
		installer.installTo(this);
	}

	@Test
	void resized() {
		fireComponentListeners(ComponentListener::componentResized);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void moved() {
		fireComponentListeners(ComponentListener::componentMoved);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void hidden() {
		fireComponentListeners(ComponentListener::componentHidden);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void shown() {
		fireComponentListeners(ComponentListener::componentShown);
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void uninstall() {

		installer.uninstallFrom(this);

		fireComponentListeners(ComponentListener::componentResized);
		fireComponentListeners(ComponentListener::componentShown);
		fireComponentListeners(ComponentListener::componentMoved);
		fireComponentListeners(ComponentListener::componentHidden);

		verifyNoInteractions(controls);
	}

	private void fireComponentListeners(BiConsumer<ComponentListener, ComponentEvent> listenerMethod) {
		int hasNoImpact = ComponentEvent.COMPONENT_RESIZED;
		ComponentEvent event = new ComponentEvent(button, hasNoImpact);
		Arrays.asList(button.getComponentListeners()).forEach(listener -> {
			listenerMethod.accept(listener, event);
		});
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}
	}
}

package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.event.ItemEvent;
import java.util.Arrays;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.BooleanValue;
import io.github.swingboot.control.installation.annotation.OnItemStateChanged;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnItemStateChangedTests {
	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@OnItemStateChanged(TestControl.class)
	private JButton anySelectedState = new JButton();

	@Test
	void anySelectedStatePerformsWhenSelected() {
		fireItemListenersOf(anySelectedState, createEvent(anySelectedState, ItemEvent.SELECTED));

		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void anySelectedStatePerformsWhenDeselected() {
		fireItemListenersOf(anySelectedState, createEvent(anySelectedState, ItemEvent.DESELECTED));

		verify(controls).perform(eq(TestControl.class));
	}

	@OnItemStateChanged(value = TestControl.class, selected = BooleanValue.TRUE)
	private JButton selectedState = new JButton();

	@Test
	void specificSelected() {
		fireItemListenersOf(selectedState, createEvent(selectedState, ItemEvent.DESELECTED));
		verifyNoInteractions(controls);

		fireItemListenersOf(selectedState, createEvent(selectedState, ItemEvent.SELECTED));
		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void uninstallation() {
		installer.uninstallFrom(this);

		fireItemListenersOf(anySelectedState, createEvent(anySelectedState, ItemEvent.SELECTED));
		verifyNoInteractions(controls);
	}

	private ItemEvent createEvent(JButton source, int selected) {
		return new ItemEvent(source, ItemEvent.ITEM_STATE_CHANGED, new String(), selected);
	}

	void fireItemListenersOf(JButton b, ItemEvent ev) {
		Arrays.asList(b.getItemListeners()).forEach(l -> {
			l.itemStateChanged(ev);
		});
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

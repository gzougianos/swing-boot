package io.github.swingboot.control.installation.factory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.BooleanValue;
import io.github.swingboot.control.installation.annotation.OnSelectionChanged;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@SuppressWarnings("unchecked")
@UiAll
class OnSelectionChangedInstallationFactoryTests {
	@OnSelectionChanged(value = TestControl.class, valueIsAdjusting = BooleanValue.TRUE)
	private int field;

	@Test
	void test() throws Exception {
		JList<String> list = new JList<>(new String[] { "hello", "world" });
		Consumer<EventObject> consumer = mock(Consumer.class);
		OnSelectionChanged annotation = getClass().getDeclaredField("field")
				.getAnnotation(OnSelectionChanged.class);

		ControlInstallation installation = new OnSelectionChangedInstallationFactory().createInstallation(annotation,
				list, consumer);

		installation.install();
		list.setSelectedValue("world", false);
		verifyZeroInteractions(consumer);

		list.setValueIsAdjusting(true);
		list.setSelectedValue("hello", false);
		verify(consumer).accept(isA(ListSelectionEvent.class));

		reset(consumer);

		installation.uninstall();
		list.setSelectedValue("world", false);
		list.setSelectedValue("hello", false);
		verifyZeroInteractions(consumer);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

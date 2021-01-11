package io.github.swingboot.control.annotation.installer;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.annotation.BooleanValue;
import io.github.swingboot.control.annotation.OnSelectionChanged;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@SuppressWarnings("unchecked")
@UiAll
class OnSelectionChangedInstallerTests {
	@OnSelectionChanged(value = TestControl.class, valueIsAdjusting = BooleanValue.TRUE)
	private int field;

	@Test
	void test() throws Exception {
		JList<String> list = new JList<>(new String[] { "hello", "world" });
		Consumer<EventObject> consumer = mock(Consumer.class);
		OnSelectionChanged annotation = getClass().getDeclaredField("field")
				.getAnnotation(OnSelectionChanged.class);

		new OnSelectionChangedInstaller().installAnnotation(annotation, list, consumer);

		list.setSelectedValue("world", false);
		verifyZeroInteractions(consumer);

		list.setValueIsAdjusting(true);
		list.setSelectedValue("hello", false);
		verify(consumer).accept(isA(ListSelectionEvent.class));
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}
